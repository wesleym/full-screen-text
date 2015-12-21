package com.wesleymoy.fullscreentext;

import android.graphics.Paint;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSizer {
    public static final int INITIAL_TEXT_SIZE = 800;
    public static final Pattern LAST_WHITESPACE = Pattern.compile("\\s\\S*$");
    private final TextPaint mTextPaint;

    public TextSizer(TextPaint textPaint) {
        mTextPaint = textPaint;
    }

    public List<String> breakLines(String input, int contentWidth, int contentHeight) {
        input = input.trim();
        mTextPaint.setTextSize(INITIAL_TEXT_SIZE);
        List<String> lines;
        while (true) {
            try {
                lines = doStuff(input, contentWidth, contentHeight);
                break;
            } catch (TooBigException e) {
                mTextPaint.setTextSize(mTextPaint.getTextSize() - 10);
            }
        }
        return lines;
    }

    private List<String> doStuff(String input, int contentWidth, int contentHeight) throws TooBigException {
        int start = 0;
        List<String> lines = new ArrayList<>();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        while (start < input.length() - 1) {
            int i = mTextPaint.breakText(input, start, input.length(), true, contentWidth, null);
            if (i == input.length() - start) {
                lines.add(input.substring(start, start + i));
                start += i;
                continue;
            }
            Matcher matcher = LAST_WHITESPACE.matcher(input).region(start, start + i);
            if (!matcher.find()) {
                throw new TooBigException();
            } else {
                int whitespace = matcher.start();
                lines.add(input.substring(start, whitespace).trim());
                start = whitespace + 1;
            }
        }

        float size = (-fontMetrics.ascent + fontMetrics.descent) * lines.size() + fontMetrics.leading * (lines.size() - 1);
        if (size > contentHeight) {
            throw new TooBigException();
        }
        return lines;
    }

    public class TooBigException extends Exception {
    }
}
