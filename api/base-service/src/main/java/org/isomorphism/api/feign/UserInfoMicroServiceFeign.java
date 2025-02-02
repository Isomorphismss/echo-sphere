package org.isomorphism.api.feign;

import org.isomorphism.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "main-service")
public interface UserInfoMicroServiceFeign {

    @PostMapping("/userinfo/updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId,
                                      @RequestParam("face") String face);

    @PostMapping("/userinfo/updateFriendCircleBg")
    public GraceJSONResult updateFriendCircleBg(
            @RequestParam("userId") String userId,
            @RequestParam("friendCircleBg") String friendCircleBg);

}
