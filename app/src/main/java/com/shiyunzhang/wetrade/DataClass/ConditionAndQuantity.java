package com.shiyunzhang.wetrade.DataClass;

public class ConditionAndQuantity {
    private String condition;
    private int quantity;

    public ConditionAndQuantity() {
    }

    public ConditionAndQuantity(String condition, int quantity){
        this.condition = condition;
        this.quantity = quantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString(){
        return "condition: " + condition + ", quantity: " + quantity;
    }
}