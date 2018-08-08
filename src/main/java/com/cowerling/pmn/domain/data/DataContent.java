package com.cowerling.pmn.domain.data;

import java.util.List;

public abstract class DataContent {
    public abstract List<String> attributeNames();
    public abstract List<Object> values();
}
