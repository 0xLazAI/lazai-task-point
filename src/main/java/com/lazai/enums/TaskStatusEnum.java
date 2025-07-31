package com.lazai.enums;

public enum TaskStatusEnum {

    PROCESSING("processing", ""),
    FINISH("finish", ""),
    INIT("init", ""),

    ;

    private final String value;
    private final String desc;

    TaskStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return this.value;
    }

    public String desc() {return this.desc;}

    public static TaskStatusEnum getByValue(String value){
        for(TaskStatusEnum result:values()){
            if(value.equals(result.value)){
                return result;
            }
        }
        return null;
    }

    public static TaskStatusEnum getByDesc(String desc){
        for(TaskStatusEnum result:values()){
            if(desc.equals(result.desc)){
                return result;
            }
        }
        return null;
    }

}
