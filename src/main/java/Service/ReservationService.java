package Service;

import Model.DTOs.CompletedAndCancelled;
import Model.DTOs.TotalAndClient;
import Model.Reservation;
import Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAll() {
        return (List<Reservation>) reservationRepository.getAll();
    }

    public Optional<Reservation> getReservation(int id) {

        return reservationRepository.getReservation(id);
    }

    public Reservation save(Reservation reservation) {
        if (reservation.getIdReservation() == null) {
            return reservationRepository.save(reservation);
        } else {
            Optional<Reservation> reservationFound = reservationRepository.getReservation(reservation.getIdReservation());
            if (reservationFound.isEmpty()) {
                return reservationRepository.save(reservation);
            } else {
                return reservation;
            }
        }
    }

    public Reservation update(Reservation reservation){
        if(reservation.getIdReservation()!= null){
            Optional<Reservation> reservationFound = reservationRepository.getReservation(reservation.getIdReservation());
            if(!reservationFound.isEmpty()){
                if(reservation.getStartDate()!= null){
                    reservationFound.get().setStartDate(reservation.getStartDate());
                }
                if(reservation.getDevolutionDate()!= null){
                    reservationFound.get().setDevolutionDate(reservation.getDevolutionDate());
                }
                return reservationRepository.save(reservationFound.get());
            }
        }
        return reservation;
    }

    public boolean deleteReservation(int reservationId){
        Boolean result = getReservation(reservationId).map(reservationToDelete ->{
            reservationRepository.delete(reservationToDelete);
            return true;
        }).orElse(false);
        return result;
    }

    //Reto 5

    public List<Reservation> getReservationsBetweenDatesReport(String dateA, String dateB){
        SimpleDateFormat parser = new SimpleDateFormat( "yyyy-MM-dd");
        Date a = new Date();
        Date b = new Date();
        try {
            a = parser.parse(dateA);
            b = parser.parse(dateB);
        } catch (ParseException exception) {
            exception.printStackTrace();
        }

        if(a.before(b)){
            return reservationRepository.getReservationsBetweenDates(a, b);
        }else{
            return new ArrayList<>();
        }
    }

    //Segundo Reporte

    public CompletedAndCancelled getReservationStatusReport(){
        List<Reservation> completed = reservationRepository.getReservationsByStatus("completed");
        List<Reservation> cancelled = reservationRepository.getReservationsByStatus("cancelled");

        int cantidadCompletadas = completed.size();
        int cantidadCanceladas = cancelled.size();

        return new CompletedAndCancelled((long) cantidadCompletadas, (long) cantidadCanceladas);
    }

    //Tercer reporte
    public List<TotalAndClient> getTopClientsReport(){
        return reservationRepository.getTopClients();
    }
}
