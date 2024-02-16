### Description:
The current jackson's object mapper is not efficient at parsing subset of json and creating pojo objects on need basis.


Consider the following structure.

```
public class DataObject {
    String name;
    String description;
    boolean needVeryhugeList
    List<Integer> veryHugeList;
    List<DataObject> veryHugeDataObjectList;
    String version;
}
```

Say we can't decide whether we need veryHugeList or veryHugeDataObjectList before parsing needVeryhugeList. Jackson does't provide a generic way to parse content optionally and going back to unparsed content and mapping it to POJO if needed.


With this library we can do exactly that, all we need to do is wrapping the fields with Lazy container

```java
public class DataObject {
    String name;
    String description;
    boolean needVeryhugeList
    Lazy<List<Integer>> veryHugeList;
    Lazy<List<DataObject>> veryHugeDataObjectList;
    String version;
}
```

To get the deserialised veryHugeDataObjectList, we just need to call something like this,

```java
ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
objectMapper.registerModule(new LazyModule(objectMapper));

LazyJson lazyJson = new LazyJson(objectMapper);
String content = readLazyFile();
DataObject dataObject = lazyJson.readValue(content, DataObject.class);

if(dataObject.needVeryhugeList) {
    // the huge list will be parsed only if you call Lazt.get() method.
    // this will avoid unncessary object creations as well as we don't need to deal JsonNode or JsonParser.
    List<DataObject> veryHugeDataObjectList = dataObject.veryHugeDataObjectList.get();
}
```
