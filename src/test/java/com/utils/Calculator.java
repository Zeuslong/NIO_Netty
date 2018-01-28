package com.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Administrator on 2018/1/26.
 */
public enum Calculator{
    Instance;
    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    public Object cal(String expression) throws ScriptException {
        return jse.eval(expression);
    }
}
