package CyStaff.app;

import CyStaff.app.Announcement.AnnouncementRepository;
import CyStaff.app.Building.BuildingRepository;
import CyStaff.app.Hour.HourRepository;
import CyStaff.app.HourType.HourTypeRepository;
import CyStaff.app.ImageData.ImageData;
import CyStaff.app.ImageData.ImageDataRepository;
import CyStaff.app.Meeting.MeetingRepository;
import CyStaff.app.Notification.NotificationRepository;
import CyStaff.app.Messaging.MessageRepository;
import CyStaff.app.Messaging.MessagingSocketConfig;
import CyStaff.app.Privilege.PrivilegeRepository;
import CyStaff.app.Room.RoomRepository;
import CyStaff.app.Settings.Settings;
import CyStaff.app.Settings.SettingsRepository;
import CyStaff.app.Status.StatusRepository;
import CyStaff.app.User.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class CyStaffApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyStaffApplication.class, args);
	}
	@Bean
	CommandLineRunner initUser(SettingsRepository settingsRepository,UserRepository userRepository, PrivilegeRepository privilegeRepository,
							   AnnouncementRepository announcementRepository, ImageDataRepository imageDataRepository,
							   HourTypeRepository hourTypeRepository, StatusRepository statusRepository,
							   HourRepository hourRepository, MessageRepository messageRepository,
							   NotificationRepository notificationRepository, BuildingRepository buildingRepository,
							   RoomRepository roomRepository, MeetingRepository meetingRepository) {
		return args -> {

		};
	}
}
