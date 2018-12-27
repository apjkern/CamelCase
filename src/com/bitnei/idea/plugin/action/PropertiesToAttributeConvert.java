package com.bitnei.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.apache.http.util.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将选中的[英文字母_英文字母]转驼峰
 * @author 智杰
 */
public class PropertiesToAttributeConvert extends AnAction {
    private static final Pattern PROPERTIES_MATCH_PATTERN = Pattern.compile("[._][a-zA-Z]");

    @Override
    public void actionPerformed(AnActionEvent e) {
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

        //格式检查
        if( !selectedText.contains("_") && !selectedText.contains(".") ){
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


    /**
     * 所有properties中的key包含. _符号的都转换为首字母大写
     * 示例
     * storm.worker.no 对应 stormWorkerNo
     * storm.kafka.spout.no 对应 stormKafkaSpoutNo
     * kafka.producer.vehicle_notice.topic 对应 kafkaProducerVehicleNoticeTopic
     *
     * @param key
     * @return
     */
    private String keyConvertAttributeName(String key) {
        key = key.toLowerCase();
        Matcher m = PROPERTIES_MATCH_PATTERN.matcher(key);
        while (m.find()) {
            String matchString = m.group();
            String upperCaseString = matchString.toUpperCase().substring(1);
            key = key.replace(matchString, upperCaseString);
        }
        return key;
    }
}
