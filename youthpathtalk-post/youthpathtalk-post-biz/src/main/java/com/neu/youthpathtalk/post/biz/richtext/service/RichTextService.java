package com.neu.youthpathtalk.post.biz.richtext.service;

import com.neu.youthpathtalk.post.biz.enums.RichTextNodeType;
import com.neu.youthpathtalk.post.biz.richtext.model.MentionAttrs;
import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import com.neu.youthpathtalk.post.biz.richtext.model.RichTextNode;
import com.neu.youthpathtalk.post.biz.rpc.UserRpcService;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien
 * @time 2026/06/23 15:34
 * @description
 */
@Component
@RequiredArgsConstructor
public class RichTextService {

    private final UserRpcService userRpcService;

    public ProcessResult process(RichTextDoc doc) {

        doc.check();

        Set<Long> mentionedUserIds = new HashSet<>();

        // Step1：收集mention userId
        for (RichTextNode node : doc.getContent()) {
            if (node.getType() == RichTextNodeType.MENTION) {
                mentionedUserIds.add(node.getAttrs().getUserId());
            }
        }

        // Step2：批量RPC
        Map<Long,String> userMap =
                userRpcService.getMentionInfoBatch(mentionedUserIds);

        // Step3：修正mention
        for (RichTextNode node : doc.getContent()) {

            if (node.getType() == RichTextNodeType.MENTION) {

                String username =
                        userMap.get(node.getAttrs().getUserId());

                if (username == null) {

                    // 降级为text
                    node.setType(RichTextNodeType.TEXT);
                    node.setText("@" + node.getAttrs().getUsername());
                    node.setAttrs(null);

                } else {
                    // 修正username快照
                    node.getAttrs().setUsername(username);
                }
            }
        }

        // Step4：生成纯文本
        String plainText = extractPlainText(doc);

        return new ProcessResult(doc, plainText, mentionedUserIds);
    }

    private String extractPlainText(RichTextDoc doc) {

        if (doc == null || CollectionUtils.isEmpty(doc.getContent())) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (RichTextNode node : doc.getContent()) {

            if (node == null || node.getType() == null) {
                continue;
            }

            switch (node.getType()) {

                case TEXT -> {
                    if (StringUtils.isNotBlank(node.getText())) {
                        sb.append(node.getText());
                    }
                }

                case MENTION -> {
                    MentionAttrs attrs = node.getAttrs();
                    if (attrs != null &&
                            StringUtils.isNotBlank(attrs.getUsername())) {
                        sb.append("@").append(attrs.getUsername());
                    }
                }
            }
        }

        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    public static class ProcessResult {
        private RichTextDoc doc;
        private String plainText;
        private Set<Long> mentionedUserIds;
    }
}