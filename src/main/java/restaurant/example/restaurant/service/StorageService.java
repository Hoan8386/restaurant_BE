package restaurant.example.restaurant.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

public interface StorageService {
    void upload(List<MultipartFile> files);

    InputStream download(String fileName);

    String getURL(String fileName);
}
