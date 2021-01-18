package com.orvillex.bortus.datapump.core.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.orvillex.bortus.datapump.core.enums.State;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 记录状态以及统计信息
 */
public class Communication {
    private Map<String, Number> counter;
    private State state;
    private Throwable throwable;
    private long timestamp;
    Map<String, List<String>> message;

    public Communication() {
        this.init();
    }

    public synchronized void reset() {
        this.init();
    }

    private void init() {
        this.counter = new ConcurrentHashMap<String, Number>();
        this.state = State.RUNNING;
        this.throwable = null;
        this.message = new ConcurrentHashMap<String, List<String>>();
        this.timestamp = System.currentTimeMillis();
    }

    public Map<String, Number> getCounter() {
        return this.counter;
    }

    public State getState() {
        return this.state;
    }

    public synchronized void setState(State state, boolean isForce) {
        if (!isForce && this.state.equals(State.FAILED)) {
            return;
        }
        this.state = state;
    }

    public synchronized void setState(State state) {
        setState(state, false);
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public synchronized String getThrowableMessage() {
        return this.throwable == null ? "" : this.throwable.getMessage();
    }

    public void setThrowable(Throwable throwable) {
        setThrowable(throwable, false);
    }

    public synchronized void setThrowable(Throwable throwable, boolean isForce) {
        if (isForce) {
            this.throwable = throwable;
        } else {
            this.throwable = this.throwable == null ? throwable : this.throwable;
        }
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, List<String>> getMessage() {
        return this.message;
    }

    public List<String> getMessage(final String key) {
        return message.get(key);
    }

    public synchronized void addMessage(final String key, final String value) {
        Validate.isTrue(StringUtils.isNotBlank(key), "增加message的key不能为空");
        List valueList = this.message.get(key);
        if (null == valueList) {
            valueList = new ArrayList<String>();
            this.message.put(key, valueList);
        }
        valueList.add(value);
    }

    public synchronized Long getLongCounter(final String key) {
        Number value = this.counter.get(key);
        return value == null ? 0 : value.longValue();
    }

    public synchronized void setLongCounter(final String key, final long value) {
        Validate.isTrue(StringUtils.isNotBlank(key), "设置counter的key不能为空");
        this.counter.put(key, value);
    }

    public synchronized Double getDoubleCounter(final String key) {
        Number value = this.counter.get(key);
        return value == null ? 0.0d : value.doubleValue();
    }

    public synchronized void setDoubleCounter(final String key, final double value) {
        Validate.isTrue(StringUtils.isNotBlank(key), "设置counter的key不能为空");
        this.counter.put(key, value);
    }

    public synchronized void increaseCounter(final String key, final long deltaValue) {
        Validate.isTrue(StringUtils.isNotBlank(key), "增加counter的key不能为空");
        long value = this.getLongCounter(key);
        this.counter.put(key, value + deltaValue);
    }

    @Override
    public Communication clone() {
        Communication communication = new Communication();

        if (this.counter != null) {
            for (Map.Entry<String, Number> entry : this.counter.entrySet()) {
                String key = entry.getKey();
                Number value = entry.getValue();
                if (value instanceof Long) {
                    communication.setLongCounter(key, (Long) value);
                } else if (value instanceof Double) {
                    communication.setDoubleCounter(key, (Double) value);
                }
            }
        }

        communication.setState(this.state, true);
        communication.setThrowable(this.throwable, true);
        communication.setTimestamp(this.timestamp);

        if (this.message != null) {
            for (final Map.Entry<String, List<String>> entry : this.message.entrySet()) {
                String key = entry.getKey();
                List value = new ArrayList() {
                    {
                        addAll(entry.getValue());
                    }
                };
                communication.getMessage().put(key, value);
            }
        }
        return communication;
    }

    public synchronized Communication mergeFrom(final Communication otherComm) {
        if (otherComm == null) {
            return this;
        }

        for (Entry<String, Number> entry : otherComm.getCounter().entrySet()) {
            String key = entry.getKey();
            Number otherValue = entry.getValue();
            if (otherValue == null) {
                continue;
            }

            Number value = this.counter.get(key);
            if (value == null) {
                value = otherValue;
            } else {
                if (value instanceof Long && otherValue instanceof Long) {
                    value = value.longValue() + otherValue.longValue();
                } else {
                    value = value.doubleValue() + value.doubleValue();
                }
            }

            this.counter.put(key, value);
        }

        mergeStateFrom(otherComm);

        this.throwable = this.throwable == null ? otherComm.getThrowable() : this.throwable;

        for (Entry<String, List<String>> entry : otherComm.getMessage().entrySet()) {
            String key = entry.getKey();
            List<String> valueList = this.message.get(key);
            if (valueList == null) {
                valueList = new ArrayList<String>();
                this.message.put(key, valueList);
            }

            valueList.addAll(entry.getValue());
        }

        return this;
    }

    public synchronized State mergeStateFrom(final Communication otherComm) {
        State retState = this.getState();
        if (otherComm == null) {
            return retState;
        }
        if (this.state == State.FAILED || otherComm.getState() == State.FAILED || this.state == State.KILLED
                || otherComm.getState() == State.KILLED) {
            retState = State.FAILED;
        } else if (this.state.isRunning() || otherComm.state.isRunning()) {
            retState = State.RUNNING;
        }
        this.setState(retState);
        return retState;
    }

    public synchronized boolean isFinished() {
        return this.state == State.SUCCEEDED || this.state == State.FAILED || this.state == State.KILLED;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object, false);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
