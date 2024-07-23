package me.zhangjh.sing.canto.controller;

import lombok.extern.slf4j.Slf4j;
import me.zhangjh.sing.canto.SyncTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/syncTool")
@Slf4j
public class SyncToolController {

    @Autowired
    private SyncTool syncTool;

    @RequestMapping("/sync")
    public void sync() {
        syncTool.syncUser();
        syncTool.syncLyric();
        syncTool.syncPracticed();
    }
}
