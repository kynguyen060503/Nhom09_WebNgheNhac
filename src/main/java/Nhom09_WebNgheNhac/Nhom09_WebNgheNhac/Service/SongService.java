package Nhom09_WebNgheNhac.Nhom09_WebNgheNhac.Service;
import Nhom09_WebNgheNhac.Nhom09_WebNgheNhac.Model.Song;
import Nhom09_WebNgheNhac.Nhom09_WebNgheNhac.Model.User;
import Nhom09_WebNgheNhac.Nhom09_WebNgheNhac.Repository.SongRepository;
import Nhom09_WebNgheNhac.Nhom09_WebNgheNhac.Repository.UserRepository;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SongService {

    private final SongRepository songRepository;

    public List<Song> searchSong(String query) {
        return songRepository.findBySongNameContainingIgnoreCase(query);
    }
    public List<Song> getAllSong(){
        return songRepository.findAll();
    }
    public Song addSong(Song song) {
        return songRepository.save(song);
    }
    public Optional<Song> getSongId(int id) {
        return songRepository.findById(id);
    }

    public Song updateSong(Song song) {
        Song existingsSong = songRepository.findById((int) song.getSongId())
                .orElseThrow(() -> new IllegalStateException("Song with ID " +
                        song.getSongId() + " does not exist."));
        existingsSong.setSongName(song.getSongName());
        existingsSong.setReleaseDate(song.getReleaseDate());
        existingsSong.setCategory(song.getCategory());
        existingsSong.setSinger(song.getSinger());
        existingsSong.setTime(song.getTime());
        existingsSong.setImage(song.getImage());
        existingsSong.setFilePath(song.getFilePath());


        return songRepository.save(existingsSong);
    }

    public void deleteSongById(int id) {
        if (!songRepository.existsById(id)) {
            throw new IllegalStateException("Song with ID " + id + " does not exist.");
        }
        songRepository.deleteById(id);
    }


    public String saveImage(MultipartFile image) throws IOException {
        File staticImagesFolder = new File("target/classes/static/images");
        if (!staticImagesFolder.exists()) {
            staticImagesFolder.mkdirs();
        }
        String fileName =image.getOriginalFilename();
        Path path = Paths.get(staticImagesFolder.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return "/images/" + fileName;
    }


    public String saveMusic(MultipartFile image) throws IOException {
        File staticImagesFolder = new File("target/classes/static/music");
        if (!staticImagesFolder.exists()) {
            staticImagesFolder.mkdirs();
        }
        String fileName =image.getOriginalFilename();
        Path path = Paths.get(staticImagesFolder.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return "/music/" + fileName;
    }

    public LocalTime timeMusic(MultipartFile mp3File) throws IOException, InvalidDataException, UnsupportedTagException {
        Path tempFile = Files.createTempFile("temp-", ".mp3");

        try {
            Files.copy(mp3File.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            Mp3File mp3 = new Mp3File(tempFile.toString());
            long durationInSeconds = mp3.getLengthInSeconds();

            int minutes = (int) (durationInSeconds / 60);
            int seconds = (int) (durationInSeconds % 60);
            LocalTime duration = LocalTime.of(0, minutes, seconds);

            return duration;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }


}
