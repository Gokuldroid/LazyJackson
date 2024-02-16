package org.gokul.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gokul.lazyjackson.Lazy;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataObject {
    String name;

    String description;

    Lazy<DataObject> dataObjectLazy;

    Lazy<String> lazyDesc;

    Lazy<List<Integer>> lazyList;

    Lazy<List<DataObject>> lazyObjectList;

    String version;
}
