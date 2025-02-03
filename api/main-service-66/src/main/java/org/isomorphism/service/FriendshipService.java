package org.isomorphism.service;

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

}
