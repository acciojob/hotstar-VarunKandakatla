package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        //Getting user and Setting his/her subscription
        User user;
        try {
            user  = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        }catch (Exception e)
        {
            return -1;
        }


        Subscription subscription  = new Subscription();
        subscription.setStartSubscriptionDate(new Date());
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setUser(user);
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        //Amount Setting
        int totalAmount = Amount(subscriptionEntryDto.getSubscriptionType());

        if(totalAmount==500)
        {
            totalAmount=totalAmount+(200*subscription.getNoOfScreensSubscribed());
        }
        else if(totalAmount==800)
        {
            totalAmount=totalAmount+(250*subscription.getNoOfScreensSubscribed());
        }
        else {
            totalAmount=totalAmount+(350*subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(totalAmount);

        userRepository.save(user);

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user ;
                try{
                    user = userRepository.findById(userId).get();
                } catch (Exception e)
                {
                    return -1;
                }

        Subscription subscription = user.getSubscription();

        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE))
        {
            throw new Exception ("Already the best Subscription");
        }

        Subscription newSubscription = updatedSubscription(subscription);

        int totalAmount = Amount(newSubscription.getSubscriptionType());

        if(totalAmount==800)
        {
            totalAmount=totalAmount+(250*newSubscription.getNoOfScreensSubscribed());
        }
        else {
            totalAmount = totalAmount + (350 * newSubscription.getNoOfScreensSubscribed());
        }

        newSubscription.setTotalAmountPaid(totalAmount);
        subscriptionRepository.save(newSubscription);

        return totalAmount-subscription.getTotalAmountPaid();
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> list = subscriptionRepository.findAll();

        if(list.size()==0) return 0;

        Integer revenue=0;

        for(Subscription subscription : list)
        {
            revenue=revenue+subscription.getTotalAmountPaid();
        }

        return revenue;
    }

    public  int  Amount(SubscriptionType subscriptionType)
    {
        if(subscriptionType.equals(SubscriptionType.BASIC))
        {
            return 500;
        }
        else if(subscriptionType.equals(SubscriptionType.PRO))
        {
            return 800;
        }

        return 1000;
    }

    public Subscription updatedSubscription( Subscription subscription)
    {
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            return subscription;
        }

        subscription.setSubscriptionType(SubscriptionType.ELITE);
        return subscription;
    }

}
