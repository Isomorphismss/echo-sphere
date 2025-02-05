package org.isomorphism.service;

import org.isomorphism.enums.YesOrNo;
import org.isomorphism.pojo.Friendship;
import org.isomorphism.pojo.vo.ContactsVO;

import java.util.List;

public interface FriendshipService {

    /**
     * 获得朋友关系
     * @param myId
     * @param friendId
     * @return
     */
    public Friendship getFriendship(String myId, String friendId);

    /**
     * 查询我的好友列表（通讯录）
     * @param myId
     * @return
     */
    public List<ContactsVO> queryMyFriends(String myId);

    /**
     * 修改我的好友的备注名
     * @param myId
     * @param friendId
     * @param friendRemark
     */
    public void updateFriendRemark(String myId,
                                   String friendId,
                                   String friendRemark);

    /**
     * 拉黑或者恢复好友
     * @param myId
     * @param friendId
     * @param yesOrNo
     */
    public void updateBlackList(String myId,
                                String friendId,
                                YesOrNo yesOrNo);

}
