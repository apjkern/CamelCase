package com.bitnei.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 智杰
 */
public class StaticVariableConvert extends AnAction {
    private static final Pattern PROPERTIES_MATCH_PATTERN = Pattern.compile("[A-Z]+");

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        //获取编辑器
        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (null == editor) {
            return;
        }
        //获取选中对象
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (TextUtils.isEmpty(selectedText)) {
            return;
        }

        //得到转换结果
        String output = keyConvertAttributeName(selectedText);

        //获取当前选中文字的start-end位置
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();

        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            editor.getDocument().deleteString(start, end);
            editor.getDocument().insertString(start, output);
            selectionModel.setSelection(start, start + output.length());
        });
    }

    private String keyConvertAttributeName(String key) {
        Matcher m = PROPERTIES_MATCH_PATTERN.matcher(key);
        while (m.find()) {
            String matchString = m.group();
            if( !key.startsWith(matchString) ){
                key = key.replace(matchString, "_" + matchString);
            }else{
                key = key.replace(matchString, matchString);
            }
        }
        return key.toUpperCase().replaceAll("_+", "_");
    }
}
