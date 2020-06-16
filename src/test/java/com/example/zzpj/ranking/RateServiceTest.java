package com.example.zzpj.ranking;
import com.example.zzpj.squad.SquadRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class RateServiceTest {

    @InjectMocks
    RateService rateService;

    @Mock
    RateRepository rateRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    SquadRepository squadRepository;
    @Mock
    User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getRateByIdShouldFailWhenRateIsNotStubbed() {
        // given
        Rate r = new Rate();
        r.setId(100L);
        r.setFkUserId(200L);
        r.setFkSquadId(300L);
        r.setRateValue(400.0);
        when(rateRepository.findById(anyLong()))
                .thenReturn(Optional.of(r));

        // when
        Rate returned = rateService.getRateById(100L);

        // then
        assertNotNull(returned);
        assertEquals(200L, returned.getFkUserId());
        assertEquals(300L, returned.getFkSquadId());
        assertEquals(400.0, returned.getRateValue());
        assertEquals(100L, returned.getId());
    }

    @Test
    void getRankingBySquadIdShouldFailWhenOrderIsNotDesc() {
        // given
        Rate r1 = new Rate();
        r1.setId(100L);
        r1.setFkUserId(200L);
        r1.setFkSquadId(300L);
        r1.setRateValue(400.0);

        Rate r2 = new Rate();
        r2.setId(101L);
        r2.setFkUserId(201L);
        r2.setFkSquadId(300L);
        r2.setRateValue(401.0);

        Rate r3 = new Rate();
        r3.setId(99L);
        r3.setFkUserId(199L);
        r3.setFkSquadId(300L);
        r3.setRateValue(399.0);

        List<Optional<Rate>> optionals = new ArrayList<>();
        optionals.add(Optional.of(r1));
        optionals.add(Optional.of(r2));
        optionals.add(Optional.of(r3));
        when(rateRepository.findAllByFkSquadId(anyLong()))
                .thenReturn(optionals);
        when(userRepository.getById(anyLong()))
                .thenReturn(user);
        when(user.getLogin())
                .thenReturn(anyString());

        // when
        List<JSONObject> jsons = rateService.getRankingBySquadId(300L);

        // then
        assertEquals(3, jsons.size());
        assertEquals(401.0, jsons.get(0).get("rate: "));
        assertEquals(400.0, jsons.get(1).get("rate: "));
        assertEquals(399.0, jsons.get(2).get("rate: "));
    }
}