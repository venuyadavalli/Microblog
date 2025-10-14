package com.microblog.services;

import com.microblog.models.Post;
import com.microblog.dto.PostView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostMapperService {

    @Autowired
    private LikeService likeService;

    public PostView toPostView(Post post, String id) {
        PostView dto = new PostView();
        dto.setId(post.getId());
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLiked(likeService.isLiked(post.getId(), id));
        dto.setLikedCount(likeService.getLikeCount(post.getId()));
        return dto;
    }
}
