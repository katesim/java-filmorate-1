package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpaById(int id) {
        Optional<Mpa> mpa = mpaStorage.loadMpaById(id);
        if (mpa.isPresent()) {
            log.debug("Load {}", mpa.get());
            return mpa.get();
        } else {
            throw new NotFoundException("MPA #" + id + " not found");
        }
    }

    public List<Mpa> getAllMpa() {
        List<Mpa> mpa = mpaStorage.loadAllMpa();
        log.debug("Load {} MPA", mpa.size());
        return mpa;
    }
}
