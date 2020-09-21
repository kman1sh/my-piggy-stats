package com.manish.statisticservice.contoller;

import com.manish.statisticservice.domain.DataPoint;
import com.manish.statisticservice.domain.shared.Account;
import com.manish.statisticservice.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

	@GetMapping(path = "/current")
	public List<DataPoint> getCurrentAccountStatistics(Principal principal) {
		return statisticsService.findByAccountName(principal.getName());
	}


    @PreAuthorize("#oauth2.hasScope('server') or #accountName.equals('demo')")
    @GetMapping(path = "/{accountName}")
    public List<DataPoint> getStatisticsByAccountName(@PathVariable String accountName) {
        return statisticsService.findByAccountName(accountName);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @PutMapping(path = "/{accountName}")
    public void saveAccountStatistics(@PathVariable String accountName, @Valid @RequestBody Account account) {

        statisticsService.save(accountName, account);
        System.out.println("successfully updated");
    }


}
