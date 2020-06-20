package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.framework.annotation.Controller;
import cn.edu.hunnu.acm.framework.annotation.GetMapping;
import cn.edu.hunnu.acm.framework.annotation.Qualifier;
import cn.edu.hunnu.acm.framework.annotation.RequestParam;
import cn.edu.hunnu.acm.model.User;
import cn.edu.hunnu.acm.service.UserService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RankController {
    @Qualifier
    private UserService userService;

    @GetMapping(value = "/rankList", produces = "application/json;charset=utf-8")
    public DataMap getRankList(@RequestParam(value = "page", defaultValue = "1") Integer page) {
        int limit = Constants.pagination.rankLimit;
        int offset = (page - 1) * limit;

        Map<String, Object> rank = userService.queryRankList(offset, limit);
        Integer userCount = (Integer) rank.get("userCount");
        List<User> userList = (List<User>) rank.get("userList");
        DataMap dataMap = new DataMap();

        dataMap.set("userCount", userCount);

        if(userList != null) {
            List<Object> userArray = new ArrayList<>();
            int ranked = offset;
            for(User user : userList) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("rank", ++ranked);
                userMap.put("userId", user.getUserId());
                userMap.put("userName", user.getUserName());
                userMap.put("whatUp", user.getWhatUp());
                userMap.put("accept", user.getAccept());
                userMap.put("submit", user.getSubmit());
                userArray.add(userMap);
            }
            dataMap.set("rankList", userArray);
        }
        return dataMap.success();
    }
}
