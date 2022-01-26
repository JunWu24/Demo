package com.junwu.demo.Utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 创建日期：2021/12/29
 *
 * @author jun.wu
 * 文件名称：LrcEntity.java.
 * 类说明：歌词解析.
 */
public class LrcEntity implements Comparable<LrcEntity> {

    private static final int MIN = 60;
    private static final int SECOND = 1000;
    private String mTime;
    private int mTimeMs;
    private String mText;

    public LrcEntity() {

    }

    /**
     * LrcEntity.
     */
    public LrcEntity(String time, String text, int timeMs) {
        this.mTime = time;
        this.mText = text;
        this.mTimeMs = timeMs;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public int getTimeMs() {
        return mTimeMs;
    }

    public void setTimeMs(int timeMs) {
        this.mTimeMs = timeMs;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    @Override
    public int compareTo(LrcEntity entity) {
        if (entity == null) {
            return -1;
        }
        return (mTimeMs - entity.getTimeMs());
    }

    /**
     * parseLrc.
     */
    public static List<LrcEntity> parseLrc(String lrcText) {
        if (TextUtils.isEmpty(lrcText)) {
            PaiAppLog.e("lrcText is null.");
            return null;
        }
        List<LrcEntity> entities = new ArrayList<>();
        String[] array = lrcText.split("\\n");
        for (String line : array) {
            List<LrcEntity> lists = parseLine(line);
            if (lists != null && !lists.isEmpty()) {
                for (LrcEntity entity : lists) {
                    PaiAppLog.v(entity.getTimeMs() + entity.getText());
                }
                entities.addAll(lists);
            }
        }
        Collections.sort(entities);
        return entities;
    }

    private static List<LrcEntity> parseLine(String line) {
        List<LrcEntity> entityList = new ArrayList<>();
        int posStart = line.indexOf("[");
        int posEnd = line.indexOf("]");
        if (posStart == 0 && posEnd != -1) {
            String[] times = new String[getCount(line)];
            String strTime = line.substring(posStart, posEnd + 1);
            times[0] = strTime;
            String text = line;
            int index = 1;
            while (posStart == 0 && posEnd != -1) {
                text = text.substring(posEnd + 1);
                posStart = text.indexOf("[");
                posEnd = text.indexOf("]");
                if (posEnd != -1) {
                    strTime = text.substring(posStart, posEnd + 1);
                    times[index] = strTime;
                    if (times[index].equals("")) {
                        return entityList;
                    }
                }
                index++;
            }
            LrcEntity lrcEntity = new LrcEntity();
            for (String time : times) {
                if (time != null) {
                    String result = text.replaceAll("<[^>]*>", "");
                    lrcEntity.setText(result);
                    if (!text.isEmpty()) {
                        lrcEntity.setTimeMs(formatTimeMs(time));
                    }
                    lrcEntity.setTime(time);
                    entityList.add(lrcEntity);
                }
            }
        }
        return entityList;
    }

    private static int formatTimeMs(String strTime) {
        int showTime = -1;
        try {
            strTime = strTime.substring(1, strTime.length() - 1);
            strTime = strTime.replace(".", ":");
            String[] times = strTime.split(":");
            showTime = (Integer.parseInt(times[0]) * MIN * SECOND)
                    + (Integer.parseInt(times[1]) * SECOND)
                    + Integer.parseInt(times[2]);
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
        return showTime;
    }

    private static int getCount(String line) {
        String[] split = line.split("]");
        return split.length;
    }
}
