package vn.id.devblog.blog_server.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.services.UploadService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private final UploadService service;

    @Autowired
    public UploadController(UploadService service) {
        this.service = service;
    }

    @GetMapping("/get-url")
    public ResponseEntity<String> getUploadUrl(
            @RequestParam("folder") String folderName,
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "contentType", required = false) String contentType //FIXME: Handle content type
    ) {
        JsonObject result = new JsonObject();
        try {
            Map<String, Object> listParam = service.generateSignature(folderName, fileName, contentType);
            if (listParam != null) {
                result = new Gson().toJsonTree(listParam).getAsJsonObject();
                result.addProperty("result", true);
            }
            else result.addProperty("result", false);
            return ResponseEntity
                    .ok()
                    .header("Content-Type", "application/json")
                    .body(result.toString());
        } catch (Exception e) {
            result.addProperty("result", false);
            return ResponseEntity
                    .status(500)
                    .header("Content-Type", "application/json")
                    .body(result.toString());
        }
    }


    @GetMapping("/upload-multiple-attachment")
    public ResponseEntity<String> uploadMultipleAttachment(
            @RequestParam("folder") String folderName,
            @RequestParam("fileName") List<String> filenames,
            @RequestParam("contentType") String contentType //FIXME: Handle content type
    ) {
        JsonObject result = new JsonObject();
        JsonArray attachments = new JsonArray();
        try {
            for (String name: filenames) {
                Map<String, Object> listParam = service.generateSignature(folderName, name, contentType);
                if (listParam != null) {
                    attachments.add(new Gson().toJsonTree(listParam));
                }
            }
            result.addProperty("result", true);
            result.add("signatures", attachments);
            return ResponseEntity
                    .ok()
                    .header("Content-Type", "application/json")
                    .body(result.toString());
        }
        catch (Exception e) {
            result.addProperty("result", false);
            return ResponseEntity
                    .status(500)
                    .header("Content-Type", "application/json")
                    .body(result.toString());
        }
    }
}