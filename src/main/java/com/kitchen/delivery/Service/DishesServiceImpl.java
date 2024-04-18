package com.kitchen.delivery.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.kitchen.delivery.DAO.DishesDao;
import com.kitchen.delivery.Model.Dishes;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DishesServiceImpl implements DishesService{
    @Autowired
    DishesDao dishesDao;


    @Autowired
    private AmazonS3 s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;


    public DishesServiceImpl(){

    }


    @Override
    public List<Dishes> getDishes() {
        return dishesDao.findAll();
    }

    @Override
    public Dishes addDishes(Dishes dishes , MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            uploadFileToS3(fileName, file);

            // Set the file address to the dishes entity
            String fileAddress = "https://" + getBucketName() + ".s3.amazonaws.com/" + fileName;
            dishes.setFileAddress(fileAddress);

            // Save the Dishes entity
            return dishesDao.save(dishes);
        } catch (IOException e) {
            log.error("Error uploading file to S3 or saving dishes", e);
            throw new RuntimeException("Failed to upload file to S3 or save dishes", e);
        }
    }

    @Override
    public Dishes updateDishes(Dishes dishes) {
        Optional<Dishes> reqDish = dishesDao.findById(dishes.getId());
        if(reqDish.isEmpty()){
            throw new EntityNotFoundException("Notes with id" + dishes.getId() + "Not available");
        }
        Dishes existingDish = reqDish.get();
        existingDish.setDishName(dishes.getDishName());
        existingDish.setPrice(dishes.getPrice());
        existingDish.setVeg(dishes.isVeg());
        existingDish.setCuisine(dishes.getCuisine());
        return dishesDao.save(existingDish);
    }

    public void deleteDish(int id){
        dishesDao.findById(id).ifPresent(dish -> dishesDao.delete(dish));
    }

    private String getBucketName(){
        return bucketName;
    }
    private void uploadFileToS3(String fileName, MultipartFile file) throws IOException {
        File fileObj = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        log.info("File uploaded to S3: {}", fileName);
    }
    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }



    ///Image


//

//
//
//    private String uploadFile(MultipartFile file){
//        File fileObj = convertMultiPartFileToFile(file);
//        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
//        s3Client.putObject(new PutObjectRequest(bucketName ,fileName,fileObj ));
//        return "File uploaded : " + fileName;
//    }
//
//    public byte[] downloadFile(String fileName){
//        S3Object s3Object = s3Client.getObject(bucketName , fileName);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//        try {
//            return IOUtils.toByteArray(inputStream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    public String deleteFile(String fileName){
//        s3Client.deleteObject(bucketName,fileName);
//        return fileName+"removed";
//    }
//    private File convertMultiPartFileToFile(MultipartFile file) {
//        File convertedFile = new File(file.getOriginalFilename());
//        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
//            fos.write(file.getBytes());
//        } catch (IOException e) {
//            log.error("Error converting multipartFile to file", e);
//        }
//        return convertedFile;
//    }

}
