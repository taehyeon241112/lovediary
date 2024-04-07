package com.lovediary.service;

import com.lovediary.dto.BookMarkTheme;
import com.lovediary.dto.BookMarkThemeDto;
import com.lovediary.repository.ThemeBookMarkRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * BookMarkService
 *
 * @author JJY
 * @version 1.0.0
 * @date 2024-04-07
 * ========================================================
 *  DATE                AUTHOR          NOTE
 * ========================================================
 *  2024-04-07          JJY             최초 등록
 **/
@Service
public class BookMarkService {
    private final ThemeBookMarkRepository themeBookMarkRepository;

    public BookMarkService(ThemeBookMarkRepository themeBookMarkRepository) {
        this.themeBookMarkRepository = themeBookMarkRepository;
    }

    @Transactional
    public List<BookMarkThemeDto> bookMarkList() {
        List<BookMarkTheme> themeBookMarkList = themeBookMarkRepository.bookMarkList();
        List<BookMarkThemeDto> resultList = new ArrayList<>();

        for(BookMarkTheme themeBookMark : themeBookMarkList) {
            resultList.add(convertToDto(themeBookMark));
        }

        return resultList;
    }

    private BookMarkThemeDto convertToDto(BookMarkTheme themeBookMark) {
        return BookMarkThemeDto.builder()
                .idx(themeBookMark.getIdx())
                .imageIdx(themeBookMark.getImageIdx())
                .name(themeBookMark.getName())
                .themeIdx(themeBookMark.getThemeIdx())
                .build();
    }
}