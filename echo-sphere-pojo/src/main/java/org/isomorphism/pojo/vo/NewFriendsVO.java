package org.isomorphism.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 好友请求记录表
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewFriendsVO implements Serializable {

    private String friendRequestId;

    private String myFriendId;

    private String myFriendFace;

    private String myFriendNickname;

    private String verifyMessage;

    private LocalDateTime requestTime;

    private Integer verifyStatus;

    private boolean isTouched = false;

}
