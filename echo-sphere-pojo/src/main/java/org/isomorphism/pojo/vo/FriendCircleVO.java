package org.isomorphism.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.isomorphism.pojo.Comment;
import org.isomorphism.pojo.FriendCircleLiked;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendCircleVO implements Serializable {

    private String FriendCircleId;

    private String userId;

    private String userNickname;

    private String userFace;

    private String words;

    private String images;

    private LocalDateTime publishTime;

    private List<FriendCircleLiked> likedFriends;   // 点赞的朋友列表

    private Boolean doILike = false;                // 用于判断当前用户是否点赞过朋友圈

    private List<Comment> commentList = new ArrayList<>();

}
