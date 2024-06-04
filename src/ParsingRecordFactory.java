import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

final class ParsingRecordFactory<T> extends ParsingFactory<T> {
    ParsingFactory<?>[] parsingFactories;

    ParsingRecordFactory(Class<? extends T> cls) {
        super(cls);

        findPatternAnnotation();
    }

    private void findPatternAnnotation() {
        Arrays.stream(this.cls.getMethods())
                .map(m -> m.getAnnotation(Parses.class))
                .filter(Objects::nonNull)
                .findFirst().ifPresent(x -> {
                    throw new InvalidDataclassException(
                            "Records may not contain @Parses annotations on methods.");
                });

        var recordAnns = this.cls.getAnnotationsByType(ParsingDataclass.class);
        argumentsAssert(recordAnns.length != 0,
                "The given record contains no @ParsingDataclass annotation.");
        argumentsAssert(recordAnns.length == 1,
                "The given record contains more than one @ParsingDataclass annotation.");

        ParsingDataclass ann = recordAnns[0];
        this.pattern = Pattern.compile(ann.value());

        var constructors = this.cls.getDeclaredConstructors();
        argumentsAssert(constructors.length == 1,
                "The given record contains more than one constructor.");

        //noinspection unchecked
        Constructor<T> constructor = (Constructor<T>) constructors[0];
        createFactory(constructor);
        createDeserializer(constructor.getParameterTypes());
    }

    private void createDeserializer(Class<?>[] types){
        parsingFactories = new ParsingFactory[types.length];
        for (int i = 0; i < parsingFactories.length; i++) {
            Class<?> type = types[i];
            if(builtinDeserializers.containsKey(type))
                continue;
            parsingFactories[i] = ParsingFactory.of(type);
        }
        deserializer = stringArgs -> {
            if(stringArgs.length != types.length)
                throw new ParsingException(
                        "The regex matched a different number of times than there " +
                                "are fields in the record: "+stringArgs.length+" instead of" +
                                " expected "+types.length
                );
            Object[] args = new Object[stringArgs.length];
            for (int i = 0; i < types.length; i++) {
                Class<?> type = types[i];
                if(builtinDeserializers.containsKey(type))
                    args[i] = builtinDeserializers.get(type).apply(stringArgs[i]);
                else
                    args[i] = parsingFactories[i].parse(stringArgs[i]);
            }
            return args;
        };
    }

    private void createFactory(Constructor<T> constructor) {
        this.factory = args -> {
            try {
                return constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ParsingException("Could not instantiate instance of record " + cls.getCanonicalName());
            }
        };
    }
}
