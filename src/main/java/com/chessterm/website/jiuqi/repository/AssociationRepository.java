package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.AssociatedPlatform;
import com.chessterm.website.jiuqi.model.Association;
import com.chessterm.website.jiuqi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociationRepository extends JpaRepository<Association, Long> {

    Association findByPlatformAndUser(AssociatedPlatform platform, User user);

    Association findByPlatformAndAssociatedId(AssociatedPlatform platform, long AssociatedId);
}
