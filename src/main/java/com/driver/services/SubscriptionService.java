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


        Subscription subscription  = user.getSubscription();

        subscription.setStartSubscriptionDate(new Date());
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setUser(user);
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        //Amount Setting
        subscription.setTotalAmountPaid(Amount(subscriptionEntryDto.getSubscriptionType()));

        userRepository.save(user);

        return Amount(subscriptionEntryDto.getSubscriptionType());
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

        subscriptionRepository.save(newSubscription);

        return Amount(newSubscription.getSubscriptionType()) - Amount (subscription.getSubscriptionType());
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
