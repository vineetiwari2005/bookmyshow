package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Enums.Gender;
import com.driver.bookMyShow.Enums.Genre;
import com.driver.bookMyShow.Enums.Language;
import com.driver.bookMyShow.Enums.UserRole;
import com.driver.bookMyShow.Models.Movie;
import com.driver.bookMyShow.Models.Theater;
import com.driver.bookMyShow.Models.User;
import com.driver.bookMyShow.Repositories.MovieRepository;
import com.driver.bookMyShow.Repositories.TheaterRepository;
import com.driver.bookMyShow.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (movieRepository.count() == 0) {
            initializeMovies();
        }
        if (theaterRepository.count() == 0) {
            initializeTheaters();
        }
        if (userRepository.count() == 0) {
            initializeUsers();
        }
    }

    private void initializeMovies() {
        Movie[] movies = {
            createMovie("Jawan", "2023-09-07", 169, Genre.ACTION, Language.HINDI, 8.5,
                "A man is driven by a personal vendetta to rectify the wrongs in society.",
                "Atlee", "Shah Rukh Khan, Nayanthara, Vijay Sethupathi",
                "https://via.placeholder.com/300x450/FF6B6B/FFFFFF?text=Jawan", 
                "https://youtube.com/watch?v=jawan"),

            createMovie("Pathaan", "2023-01-25", 146, Genre.ACTION, Language.HINDI, 8.2,
                "An Indian spy takes on the leader of a terrorist organization.",
                "Siddharth Anand", "Shah Rukh Khan, Deepika Padukone, John Abraham",
                "https://via.placeholder.com/300x450/4ECDC4/FFFFFF?text=Pathaan", 
                "https://youtube.com/watch?v=pathaan"),

            createMovie("RRR", "2022-03-25", 187, Genre.ACTION, Language.TELUGU, 8.8,
                "A fictitious story of two legendary revolutionaries.",
                "S.S. Rajamouli", "N.T. Rama Rao Jr., Ram Charan, Alia Bhatt",
                "https://via.placeholder.com/300x450/FFE66D/000000?text=RRR", 
                "https://youtube.com/watch?v=rrr"),

            createMovie("KGF Chapter 2", "2022-04-14", 166, Genre.ACTION, Language.KANNADA, 8.4,
                "Rocky continues his journey to be the most powerful.",
                "Prashanth Neel", "Yash, Sanjay Dutt, Raveena Tandon",
                "https://via.placeholder.com/300x450/A8E6CF/000000?text=KGF+2", 
                "https://youtube.com/watch?v=kgf2"),

            createMovie("Salaar", "2023-12-22", 175, Genre.ACTION, Language.TELUGU, 8.1,
                "A gang leader tries to keep a promise made to his dying friend.",
                "Prashanth Neel", "Prabhas, Prithviraj Sukumaran, Shruti Haasan",
                "https://via.placeholder.com/300x450/FF6B9D/FFFFFF?text=Salaar", 
                "https://youtube.com/watch?v=salaar"),

            createMovie("Leo", "2023-10-19", 164, Genre.ACTION, Language.TAMIL, 7.9,
                "A mild-mannered cafe owner's peaceful life is shaken.",
                "Lokesh Kanagaraj", "Vijay, Trisha, Sanjay Dutt",
                "https://via.placeholder.com/300x450/C7CEEA/000000?text=Leo", 
                "https://youtube.com/watch?v=leo"),

            createMovie("Jailer", "2023-08-10", 168, Genre.ACTION, Language.TAMIL, 8.3,
                "A retired jailer goes on a manhunt to find his son's killers.",
                "Nelson Dilipkumar", "Rajinikanth, Mohanlal, Jackie Shroff",
                "https://via.placeholder.com/300x450/FFDAB9/000000?text=Jailer", 
                "https://youtube.com/watch?v=jailer"),

            createMovie("Vikram", "2022-06-03", 174, Genre.ACTION, Language.TAMIL, 8.5,
                "Members of a black ops team must track and eliminate a gang of masked murderers.",
                "Lokesh Kanagaraj", "Kamal Haasan, Vijay Sethupathi, Fahadh Faasil",
                "https://via.placeholder.com/300x450/B4A7D6/FFFFFF?text=Vikram", 
                "https://youtube.com/watch?v=vikram"),

            createMovie("Pushpa: The Rise", "2021-12-17", 179, Genre.ACTION, Language.TELUGU, 7.8,
                "A laborer rises through the ranks of a red sandalwood smuggling syndicate.",
                "Sukumar", "Allu Arjun, Rashmika Mandanna, Fahadh Faasil",
                "https://via.placeholder.com/300x450/E8B4B8/000000?text=Pushpa", 
                "https://youtube.com/watch?v=pushpa"),

            createMovie("Tiger 3", "2023-11-12", 155, Genre.ACTION, Language.HINDI, 7.5,
                "Tiger and Zoya face their biggest threat yet.",
                "Maneesh Sharma", "Salman Khan, Katrina Kaif, Emraan Hashmi",
                "https://via.placeholder.com/300x450/FFB347/000000?text=Tiger+3", 
                "https://youtube.com/watch?v=tiger3"),

            createMovie("Dunki", "2023-12-21", 161, Genre.DRAMA, Language.HINDI, 7.6,
                "A story of illegal immigration and the struggles of those seeking a better life.",
                "Rajkumar Hirani", "Shah Rukh Khan, Taapsee Pannu, Vicky Kaushal",
                "https://via.placeholder.com/300x450/77DD77/000000?text=Dunki", 
                "https://youtube.com/watch?v=dunki"),

            createMovie("12th Fail", "2023-10-27", 147, Genre.DRAMA, Language.HINDI, 9.1,
                "Based on the true story of an IPS officer's journey.",
                "Vidhu Vinod Chopra", "Vikrant Massey, Medha Shankar",
                "https://via.placeholder.com/300x450/88B04B/FFFFFF?text=12th+Fail", 
                "https://youtube.com/watch?v=12thfail"),

            createMovie("Animal", "2023-12-01", 201, Genre.THRILLER, Language.HINDI, 7.7,
                "A son's journey to avenge the attempted assassination of his father.",
                "Sandeep Reddy Vanga", "Ranbir Kapoor, Anil Kapoor, Bobby Deol",
                "https://via.placeholder.com/300x450/CC0000/FFFFFF?text=Animal", 
                "https://youtube.com/watch?v=animal"),

            createMovie("Gadar 2", "2023-08-11", 170, Genre.ROMANTIC, Language.HINDI, 7.3,
                "Tara Singh goes to Pakistan to rescue his son.",
                "Anil Sharma", "Sunny Deol, Ameesha Patel, Utkarsh Sharma",
                "https://via.placeholder.com/300x450/FF9999/000000?text=Gadar+2", 
                "https://youtube.com/watch?v=gadar2"),

            createMovie("OMG 2", "2023-08-11", 155, Genre.SOCIAL, Language.HINDI, 8.0,
                "A satire on sex education and the Indian education system.",
                "Amit Rai", "Akshay Kumar, Pankaj Tripathi, Yami Gautam",
                "https://via.placeholder.com/300x450/FFCC5C/000000?text=OMG+2", 
                "https://youtube.com/watch?v=omg2"),

            createMovie("Oppenheimer", "2023-07-21", 180, Genre.HISTORICAL, Language.ENGLISH, 8.6,
                "The story of J. Robert Oppenheimer's role in developing the atomic bomb.",
                "Christopher Nolan", "Cillian Murphy, Emily Blunt, Robert Downey Jr.",
                "https://via.placeholder.com/300x450/2C3E50/FFFFFF?text=Oppenheimer", 
                "https://youtube.com/watch?v=oppenheimer"),

            createMovie("Barbie", "2023-07-21", 114, Genre.COMEDY, Language.ENGLISH, 7.4,
                "Barbie and Ken are having the time of their lives in Barbie Land.",
                "Greta Gerwig", "Margot Robbie, Ryan Gosling, Will Ferrell",
                "https://via.placeholder.com/300x450/FF69B4/FFFFFF?text=Barbie", 
                "https://youtube.com/watch?v=barbie"),

            createMovie("Kantara", "2022-09-30", 148, Genre.THRILLER, Language.KANNADA, 8.4,
                "A local Kambala champion faces a dispute with a forest officer.",
                "Rishab Shetty", "Rishab Shetty, Sapthami Gowda, Kishore",
                "https://via.placeholder.com/300x450/8B4513/FFFFFF?text=Kantara", 
                "https://youtube.com/watch?v=kantara"),

            createMovie("Ponniyin Selvan 1", "2022-09-30", 167, Genre.HISTORICAL, Language.TAMIL, 7.8,
                "The early life of Chola prince Arulmozhi Varman.",
                "Mani Ratnam", "Vikram, Aishwarya Rai, Jayam Ravi",
                "https://via.placeholder.com/300x450/DAA520/000000?text=PS1", 
                "https://youtube.com/watch?v=ps1"),

            createMovie("Brahmastra", "2022-09-09", 167, Genre.ANIMATION, Language.HINDI, 6.8,
                "A DJ discovers his strange connection to the elemental forces of nature.",
                "Ayan Mukerji", "Ranbir Kapoor, Alia Bhatt, Amitabh Bachchan",
                "https://via.placeholder.com/300x450/FF6347/FFFFFF?text=Brahmastra", 
                "https://youtube.com/watch?v=brahmastra")
        };

        for (Movie movie : movies) {
            movieRepository.save(movie);
        }
        System.out.println("✅ Initialized " + movies.length + " movies");
    }

    private void initializeTheaters() {
        String[][] theaterData = {
            // Mumbai
            {"PVR Phoenix Palladium", "Lower Parel, Mumbai", "Mumbai"},
            {"INOX Nariman Point", "Nariman Point, Mumbai", "Mumbai"},
            {"Cinepolis Andheri", "Andheri West, Mumbai", "Mumbai"},
            {"PVR ECX Chembur", "Chembur, Mumbai", "Mumbai"},
            {"INOX Megaplex Inorbit Mall", "Malad, Mumbai", "Mumbai"},

            // Delhi
            {"PVR Saket", "Select Citywalk Mall, Saket, New Delhi", "Delhi"},
            {"INOX Nehru Place", "Nehru Place, New Delhi", "Delhi"},
            {"Cinepolis DLF Place", "DLF Place Mall, Saket, New Delhi", "Delhi"},
            {"PVR Priya", "Vasant Vihar, New Delhi", "Delhi"},
            {"INOX Connaught Place", "Connaught Place, New Delhi", "Delhi"},

            // Bangalore
            {"PVR Forum Mall", "Koramangala, Bangalore", "Bangalore"},
            {"INOX Garuda Mall", "Magrath Road, Bangalore", "Bangalore"},
            {"Cinepolis Nexus Shantiniketan", "Whitefield, Bangalore", "Bangalore"},
            {"PVR Orion Mall", "Rajajinagar, Bangalore", "Bangalore"},
            {"INOX Lido Mall", "MG Road, Bangalore", "Bangalore"},

            // Chennai
            {"PVR Grand Galleria", "Pallavaram, Chennai", "Chennai"},
            {"INOX Escape", "Express Avenue, Chennai", "Chennai"},
            {"Cinepolis INORBIT Mall", "Malumichampatti, Chennai", "Chennai"},
            {"PVR Heritage RSL", "Anna Salai, Chennai", "Chennai"},
            {"INOX National", "Arcot Road, Chennai", "Chennai"},

            // Hyderabad
            {"PVR Next Galleria", "Panjagutta, Hyderabad", "Hyderabad"},
            {"INOX GSM Mall", "Masab Tank, Hyderabad", "Hyderabad"},
            {"Cinepolis Mantra Mall", "Attapur, Hyderabad", "Hyderabad"},
            {"PVR Irrum Manzil", "Somajiguda, Hyderabad", "Hyderabad"},
            {"INOX GVK One", "Banjara Hills, Hyderabad", "Hyderabad"},

            // Kolkata
            {"PVR Avani Riverside", "Howrah, Kolkata", "Kolkata"},
            {"INOX South City", "Prince Anwar Shah Road, Kolkata", "Kolkata"},
            {"Cinepolis Lake Mall", "Jessore Road, Kolkata", "Kolkata"},
            {"PVR Mani Square", "EM Bypass, Kolkata", "Kolkata"},
            {"INOX Forum", "Elgin Road, Kolkata", "Kolkata"},

            // Pune
            {"PVR Phoenix Marketcity", "Viman Nagar, Pune", "Pune"},
            {"INOX Bund Garden", "Bund Garden Road, Pune", "Pune"},
            {"Cinepolis Westend Mall", "Aundh, Pune", "Pune"},
            {"PVR Pavillion Mall", "Shivajinagar, Pune", "Pune"},
            {"INOX SGS Mall", "Camp, Pune", "Pune"}
        };

        for (String[] data : theaterData) {
            Theater theater = Theater.builder()
                    .name(data[0])
                    .address(data[1])
                    .city(data[2])
                    .build();
            theaterRepository.save(theater);
        }
        System.out.println("✅ Initialized " + theaterData.length + " theaters");
    }

    private void initializeUsers() {
        User admin = User.builder()
                .name("Admin User")
                .emailId("admin@bookmyshow.com")
                .password(passwordEncoder.encode("admin123"))
                .mobileNo("9999999999")
                .age(30)
                .gender(Gender.MALE)
                .role(UserRole.ADMIN)
                .isActive(true)
                .walletBalance(0.0)
                .build();

        User testUser = User.builder()
                .name("Test User")
                .emailId("test@example.com")
                .password(passwordEncoder.encode("test123"))
                .mobileNo("8888888888")
                .age(25)
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .isActive(true)
                .walletBalance(500.0)
                .build();

        userRepository.save(admin);
        userRepository.save(testUser);
        System.out.println("✅ Initialized 2 users (admin & test)");
    }

    private Movie createMovie(String name, String releaseDate, int duration, Genre genre,
                             Language language, double rating, String description,
                             String director, String cast, String posterUrl, String trailerUrl) {
        return Movie.builder()
                .movieName(name)
                .releaseDate(Date.valueOf(LocalDate.parse(releaseDate)))
                .duration(duration)
                .genre(genre)
                .language(language)
                .rating(rating)
                .description(description)
                .director(director)
                .cast(cast)
                .posterUrl(posterUrl)
                .trailerUrl(trailerUrl)
                .nowShowing(true)
                .build();
    }
}
