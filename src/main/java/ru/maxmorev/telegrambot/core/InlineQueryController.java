package ru.maxmorev.telegrambot.core;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public class InlineQueryController {
    final static Logger logger = Logger.getLogger(InlineQueryController.class);
	/**
     * For an InlineQuery, results from RAE dictionariy are fetch and returned
     * @param inlineQuery InlineQuery recieved
     */
    public void InlineQueryMapper(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        logger.debug(query);
        if (!query.isEmpty()) {
		    //List<RaeService.RaeResult> results = raeService.getResults(query);
		    //answerInlineQuery(converteResultsToResponse(inlineQuery, results));
		} else {
		    //answerInlineQuery(converteResultsToResponse(inlineQuery, new ArrayList<>()));
		}
    }

}
