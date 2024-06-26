package com.lovediary.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovediary.dto.*;
import com.lovediary.service.BalanceService;
import com.lovediary.util.Session;
import com.lovediary.values.ResponseData;
import com.lovediary.values.constValues;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * BalanceRestController
 *
 * @author HTH
 * @version 1.0.0
 * @date 2024-04-09
 * ========================================================
 *  DATE                AUTHOR          NOTE
 * ========================================================
 *  2024-04-09          HTH             최초 등록
 **/
@RestController
public class BalanceRestController extends Session {
    private final BalanceService balanceService;
    public BalanceRestController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    // 저장
    @PostMapping("/balance/save")
    public ResponseData save(HttpServletRequest request,
                             @RequestParam(name = "idx", required = false) Long idx,
                             @RequestParam(name = "title") String title,
                             @RequestParam(name = "contents") String contents,
                             @RequestParam(name = "item_list") String itemListStr) throws JsonProcessingException {
        if(title == null || title.isEmpty()) {
            return new ResponseData(constValues.ERROR, "제목을 입력해주세요.", null);
        }

        if(contents == null || contents.isEmpty()) {
            return new ResponseData(constValues.ERROR, "내용을 입력해주세요.", null);
        }

        ObjectMapper mapper = new ObjectMapper();
        List<BalanceItemDto> itemList = mapper.readValue(itemListStr, new TypeReference<List<BalanceItemDto>>() {});

        BalanceDto balance = null;
        if(idx != null) {
            balance = balanceService.getOne(idx);
            balance.setTitle(title);
            balance.setContents(contents);
            balance.setModifyDate(new Timestamp(System.currentTimeMillis()));
        } else {
            balance = BalanceDto.builder()
                    .title(title)
                    .contents(contents)
                    .accountIdx(this.getLoginData(request).getAccountIdx())
                    .build();
        }

        Long result = balanceService.saveItem(balance);
        for(BalanceItemDto balanceItem : itemList) {
            balanceItem.setBalanceIdx(result);
            balanceService.saveItem(balanceItem);
        }

        return new ResponseData(constValues.DONE, "저장되었습니다.", result);
    }

    // 삭제
    @PostMapping("/balance/remove")
    public ResponseData remove(HttpServletRequest request,
                               @RequestParam(name = "idx", required = false) Long idx) {
        BalanceDto balance = balanceService.getOne(idx);
        balance.setDeleteYn('Y');
        balance.setDeleteDate(new Timestamp(System.currentTimeMillis()));

        balanceService.saveItem(balance);

        return new ResponseData(constValues.DONE, "삭제되었습니다.", null);
    }

    // 선택 저장
    @PostMapping("/balance/save_answer")
    public ResponseData saveSelect(HttpServletRequest request,
                                   @RequestParam(name = "idx") Long idx,
                                   @RequestParam(name = "item_idx", required = false) Long itemIdx) {
        if(idx == null || itemIdx == null) {
            return new ResponseData(constValues.ERROR, "실패", null);
        }

        Long accountIdx = this.getLoginData(request).getAccountIdx();

        BalanceAnswerDto answer = balanceService.getAnswer(idx, accountIdx);
        if(answer == null) {
            answer = BalanceAnswerDto.builder()
                        .balanceIdx(idx)
                        .accountIdx(accountIdx)
                        .build();
        }

        answer.setBalanceItemIdx(itemIdx);

        balanceService.saveAnswer(answer);

        return new ResponseData(constValues.DONE, "성공", null);
    }

    // 댓글 저장
    @PostMapping("/balance/save_comment")
    public ResponseData saveComment(HttpServletRequest request,
                                    @RequestParam(name = "idx", required = false) Long idx,
                                    @RequestParam(name = "balance_idx", required = false) Long balanceIdx,
                                    @RequestParam(name = "reply_idx", required = false) Long replyIdx,
                                    @RequestParam(name = "contents", required = false) String contents) {
        if(contents == null || contents.isEmpty()) {
            return new ResponseData(constValues.ERROR, "내용을 입력해주세요.", null);
        }

        if(replyIdx != null && replyIdx < 1) {
            replyIdx = null;
        }

        BalanceReplyDto replyDto = null;
        if(idx == null) {
            replyDto = BalanceReplyDto.builder()
                    .balanceIdx(balanceIdx)
                    .replyIdx(replyIdx)
                    .contents(contents)
                    .accountIdx(this.getLoginData(request).getAccountIdx())
                    .build();
        } else {
            replyDto = balanceService.getCommentOne(idx);
            replyDto.setContents(contents);
        }

        Long result = balanceService.saveComment(replyDto);

        return new ResponseData(constValues.DONE, "댓글이 저장되었습니다.", result);
    }

    @PostMapping("/balance/remove_comment")
    public ResponseData removeComment(@RequestParam(name = "idx") Long idx) {
        BalanceReplyDto replyDto = balanceService.getCommentOne(idx);
        replyDto.setDeleteYn('Y');
        replyDto.setDeleteDate(new Timestamp(System.currentTimeMillis()));

        Long result = balanceService.saveComment(replyDto);

        return new ResponseData(constValues.DONE, "댓글이 삭제되었습니다.", result);
    }
}
