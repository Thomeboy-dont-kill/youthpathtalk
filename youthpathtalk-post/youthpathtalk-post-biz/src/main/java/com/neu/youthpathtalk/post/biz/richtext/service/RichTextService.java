package com.neu.youthpathtalk.post.biz.richtext.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.youthpathtalk.post.biz.enums.RichTextNodeType;
import com.neu.youthpathtalk.post.biz.richtext.model.attrs.ImageAttrs;
import com.neu.youthpathtalk.post.biz.richtext.model.attrs.MentionAttrs;
import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import com.neu.youthpathtalk.post.biz.richtext.model.RichTextNode;
import com.neu.youthpathtalk.post.biz.richtext.model.attrs.VideoAttrs;
import com.neu.youthpathtalk.post.biz.rpc.UserRpcService;
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
    private final ObjectMapper objectMapper;

    private final UserRpcService userRpcService;

    //可添加枚举类来适应不同功能的富文本处理要求
    public ProcessResult process(RichTextDoc doc) {

        doc.check();

        for (RichTextNode node : doc.getContent()) {

            switch (node.getType()) {
                case TEXT -> {}

                case MENTION -> {

                    MentionAttrs attrs =
                            attrs(node, MentionAttrs.class);

                    attrs.check();
                }

                case IMAGE -> {

                    ImageAttrs attrs =
                            attrs(node, ImageAttrs.class);

                    attrs.check();
                }

                case VIDEO -> {

                    VideoAttrs attrs =
                            attrs(node, VideoAttrs.class);

                    attrs.check();
                }
            }
        }

        Set<Long> mentionedUserIds = new HashSet<>();

        // Step1：收集mention userId
        for (RichTextNode node : doc.getContent()) {
            if (node.getType() == RichTextNodeType.MENTION) {

                MentionAttrs attrs = attrs(node, MentionAttrs.class);

                mentionedUserIds.add(attrs.getUserId());
            }
        }

        // Step2：批量RPC
        Map<Long,String> userMap =
                userRpcService.getMentionInfoBatch(mentionedUserIds);

        // Step3：修正mention
        for (RichTextNode node : doc.getContent()) {

            if (node.getType() == RichTextNodeType.MENTION) {

                MentionAttrs attrs = attrs(node, MentionAttrs.class);

                String username = userMap.get(attrs.getUserId());

                if (username == null) {

                    node.setType(RichTextNodeType.TEXT);
                    node.setText("@" + attrs.getUsername());
                    node.setAttrs(null);

                } else {

                    attrs.setUsername(username);
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
                    MentionAttrs attrs = attrs(node, MentionAttrs.class);
                    if (attrs != null &&
                            StringUtils.isNotBlank(attrs.getUsername())) {
                        sb.append("@").append(attrs.getUsername());
                    }
                }

                case IMAGE, VIDEO -> {
                    //什么也不做
                }
            }
        }

        return sb.toString();
    }

    private void validateNode(RichTextNode node) {

        switch (node.getType()) {

            case MENTION -> {

                MentionAttrs attrs =
                        attrs(node, MentionAttrs.class);

                attrs.check();
            }

            case IMAGE,VIDEO -> {
                //暂时不做ImageAttrs, VideoAttrs业务强校验
            }
        }
    }

    private <T> T attrs(RichTextNode node, Class<T> clazz) {
        return objectMapper.convertValue(node.getAttrs(), clazz);
    }

    @Data
    @AllArgsConstructor
    public static class ProcessResult {
        private RichTextDoc doc;
        private String plainText;
        private Set<Long> mentionedUserIds;
    }
}