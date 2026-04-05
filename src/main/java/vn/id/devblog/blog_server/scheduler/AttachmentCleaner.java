package vn.id.devblog.blog_server.scheduler;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AttachmentCleaner {

    @Autowired
    private Cloudinary cloudinary;

    // Chạy vào 2h sáng mỗi ngày
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupUnusedImages() throws Exception {
        // Xóa tất cả các ảnh có tag 'pressblog_unverified'
        // mà đã được upload hơn 24 giờ trước
        cloudinary.api().deleteResourcesByTag("pressblog_unverified",
                ObjectUtils.asMap("keep_original", false));
        log.info("Clean attachment success!");
    }
}