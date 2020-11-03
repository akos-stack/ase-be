package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Owner;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.OwnerRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BaseEntityTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private OwnerRepository ownerRepository;

    @Test(expected = NullPointerException.class)
    public void prePersist_creator_isNull() {
        Owner owner = new Owner();
        owner.setUserProfile(mockUtil.savedUser());
        owner.setBirthday(LocalDate.now().minusYears(20));
        ownerRepository.saveAndFlush(owner);
    }

    @Test
    public void prePersist_creator_isNotNull() {
        Owner owner = new Owner();
        UserProfile creator = mockUtil.savedUser();
        owner.setUserProfile(creator);
        owner.setBirthday(LocalDate.now().minusYears(20));
        owner.setCreator(creator.getId());
        assertNull(owner.getCreated());
        owner = ownerRepository.saveAndFlush(owner);
        assertNotNull(owner.getCreated());
    }

    @Test(expected = NullPointerException.class)
    public void preUpdate_updater_isNull() {
        Owner oldOwner = new Owner();
        {
            UserProfile creator = mockUtil.savedUser();
            oldOwner.setUserProfile(creator);
            oldOwner.setBirthday(LocalDate.now());
            oldOwner.setCreator(creator.getId());
            ownerRepository.saveAndFlush(oldOwner);
        }
        Owner newOwner = new Owner();
        {
            newOwner.setId(oldOwner.getId());
            newOwner.setUserProfile(oldOwner.getUserProfile());
            newOwner.setBirthday(LocalDate.now().minusYears(20)); // update
            MockUtil.copyBaseEntityData(oldOwner, newOwner);
            ownerRepository.saveAndFlush(newOwner);
        }
    }

    @Test
    public void preUpdate_updater_isNotNull() {
        Owner oldOwner = new Owner();
        {
            UserProfile creator = mockUtil.savedUser();
            oldOwner.setUserProfile(creator);
            oldOwner.setBirthday(LocalDate.now());
            oldOwner.setCreator(creator.getId());
            ownerRepository.saveAndFlush(oldOwner);
        }
        Owner newOwner = new Owner();
        {
            newOwner.setId(oldOwner.getId());
            newOwner.setUserProfile(oldOwner.getUserProfile());
            newOwner.setBirthday(LocalDate.now().minusYears(20)); // update
            MockUtil.copyBaseEntityData(oldOwner, newOwner);
            newOwner.setUpdater(newOwner.getCreator()); // !null
            assertNull(newOwner.getUpdated());
            newOwner = ownerRepository.saveAndFlush(newOwner);
            assertNotNull(newOwner.getUpdated());
        }
    }

}
