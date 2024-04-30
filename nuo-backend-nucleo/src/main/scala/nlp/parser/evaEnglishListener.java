// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/evaEnglish.g4 by ANTLR 4.7
package nlp.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link evaEnglishParser}.
 */
public interface evaEnglishListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code commandSelect}
	 * labeled alternative in {@link evaEnglishParser#question}.
	 * @param ctx the parse tree
	 */
	void enterCommandSelect(evaEnglishParser.CommandSelectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commandSelect}
	 * labeled alternative in {@link evaEnglishParser#question}.
	 * @param ctx the parse tree
	 */
	void exitCommandSelect(evaEnglishParser.CommandSelectContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#selectClause}.
	 * @param ctx the parse tree
	 */
	void enterSelectClause(evaEnglishParser.SelectClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#selectClause}.
	 * @param ctx the parse tree
	 */
	void exitSelectClause(evaEnglishParser.SelectClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#fieldList}.
	 * @param ctx the parse tree
	 */
	void enterFieldList(evaEnglishParser.FieldListContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#fieldList}.
	 * @param ctx the parse tree
	 */
	void exitFieldList(evaEnglishParser.FieldListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaParen}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaParen(evaEnglishParser.CriteriaParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaParen}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaParen(evaEnglishParser.CriteriaParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaNegative}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaNegative(evaEnglishParser.CriteriaNegativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaNegative}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaNegative(evaEnglishParser.CriteriaNegativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaPositive}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaPositive(evaEnglishParser.CriteriaPositiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaPositive}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaPositive(evaEnglishParser.CriteriaPositiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaWhereDefault}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaWhereDefault(evaEnglishParser.CriteriaWhereDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaWhereDefault}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaWhereDefault(evaEnglishParser.CriteriaWhereDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaAnd}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaAnd(evaEnglishParser.CriteriaAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaAnd}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaAnd(evaEnglishParser.CriteriaAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaWhereOrphanNegative}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaWhereOrphanNegative(evaEnglishParser.CriteriaWhereOrphanNegativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaWhereOrphanNegative}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaWhereOrphanNegative(evaEnglishParser.CriteriaWhereOrphanNegativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaWhereOrphanDefault}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaWhereOrphanDefault(evaEnglishParser.CriteriaWhereOrphanDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaWhereOrphanDefault}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaWhereOrphanDefault(evaEnglishParser.CriteriaWhereOrphanDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaOr}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaOr(evaEnglishParser.CriteriaOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaOr}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaOr(evaEnglishParser.CriteriaOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereOrphanBetween}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereOrphanBetween(evaEnglishParser.WhereOrphanBetweenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereOrphanBetween}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereOrphanBetween(evaEnglishParser.WhereOrphanBetweenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereOrphanGt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereOrphanGt(evaEnglishParser.WhereOrphanGtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereOrphanGt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereOrphanGt(evaEnglishParser.WhereOrphanGtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereOrphanLt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereOrphanLt(evaEnglishParser.WhereOrphanLtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereOrphanLt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereOrphanLt(evaEnglishParser.WhereOrphanLtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereOrphanGtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereOrphanGtEq(evaEnglishParser.WhereOrphanGtEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereOrphanGtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereOrphanGtEq(evaEnglishParser.WhereOrphanGtEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereOrphanLtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereOrphanLtEq(evaEnglishParser.WhereOrphanLtEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereOrphanLtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereOrphanLtEq(evaEnglishParser.WhereOrphanLtEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereOrphanEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereOrphanEq(evaEnglishParser.WhereOrphanEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereOrphanEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereOrphanEq(evaEnglishParser.WhereOrphanEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dateFieldDateTime}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 */
	void enterDateFieldDateTime(evaEnglishParser.DateFieldDateTimeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dateFieldDateTime}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 */
	void exitDateFieldDateTime(evaEnglishParser.DateFieldDateTimeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dateFieldDateOnly}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 */
	void enterDateFieldDateOnly(evaEnglishParser.DateFieldDateOnlyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dateFieldDateOnly}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 */
	void exitDateFieldDateOnly(evaEnglishParser.DateFieldDateOnlyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dateFieldTimeOnly}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 */
	void enterDateFieldTimeOnly(evaEnglishParser.DateFieldTimeOnlyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dateFieldTimeOnly}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 */
	void exitDateFieldTimeOnly(evaEnglishParser.DateFieldTimeOnlyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereBetween}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereBetween(evaEnglishParser.WhereBetweenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereBetween}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereBetween(evaEnglishParser.WhereBetweenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereGtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereGtEq(evaEnglishParser.WhereGtEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereGtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereGtEq(evaEnglishParser.WhereGtEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereLtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereLtEq(evaEnglishParser.WhereLtEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereLtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereLtEq(evaEnglishParser.WhereLtEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereGt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereGt(evaEnglishParser.WhereGtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereGt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereGt(evaEnglishParser.WhereGtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereLt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereLt(evaEnglishParser.WhereLtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereLt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereLt(evaEnglishParser.WhereLtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereDuration}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereDuration(evaEnglishParser.WhereDurationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereDuration}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereDuration(evaEnglishParser.WhereDurationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereDateEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereDateEq(evaEnglishParser.WhereDateEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereDateEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereDateEq(evaEnglishParser.WhereDateEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereStringFunctions}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereStringFunctions(evaEnglishParser.WhereStringFunctionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereStringFunctions}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereStringFunctions(evaEnglishParser.WhereStringFunctionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereDefault}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereDefault(evaEnglishParser.WhereDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereDefault}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereDefault(evaEnglishParser.WhereDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereDefaultNegative}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereDefaultNegative(evaEnglishParser.WhereDefaultNegativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereDefaultNegative}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereDefaultNegative(evaEnglishParser.WhereDefaultNegativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whereEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void enterWhereEq(evaEnglishParser.WhereEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whereEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 */
	void exitWhereEq(evaEnglishParser.WhereEqContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#negativeVerb}.
	 * @param ctx the parse tree
	 */
	void enterNegativeVerb(evaEnglishParser.NegativeVerbContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#negativeVerb}.
	 * @param ctx the parse tree
	 */
	void exitNegativeVerb(evaEnglishParser.NegativeVerbContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#positiveVerb}.
	 * @param ctx the parse tree
	 */
	void enterPositiveVerb(evaEnglishParser.PositiveVerbContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#positiveVerb}.
	 * @param ctx the parse tree
	 */
	void exitPositiveVerb(evaEnglishParser.PositiveVerbContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#comparisionPhrases}.
	 * @param ctx the parse tree
	 */
	void enterComparisionPhrases(evaEnglishParser.ComparisionPhrasesContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#comparisionPhrases}.
	 * @param ctx the parse tree
	 */
	void exitComparisionPhrases(evaEnglishParser.ComparisionPhrasesContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#unavailablePhrases}.
	 * @param ctx the parse tree
	 */
	void enterUnavailablePhrases(evaEnglishParser.UnavailablePhrasesContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#unavailablePhrases}.
	 * @param ctx the parse tree
	 */
	void exitUnavailablePhrases(evaEnglishParser.UnavailablePhrasesContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#duration}.
	 * @param ctx the parse tree
	 */
	void enterDuration(evaEnglishParser.DurationContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#duration}.
	 * @param ctx the parse tree
	 */
	void exitDuration(evaEnglishParser.DurationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code durationFieldPast}
	 * labeled alternative in {@link evaEnglishParser#durationField}.
	 * @param ctx the parse tree
	 */
	void enterDurationFieldPast(evaEnglishParser.DurationFieldPastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code durationFieldPast}
	 * labeled alternative in {@link evaEnglishParser#durationField}.
	 * @param ctx the parse tree
	 */
	void exitDurationFieldPast(evaEnglishParser.DurationFieldPastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code durationFieldNext}
	 * labeled alternative in {@link evaEnglishParser#durationField}.
	 * @param ctx the parse tree
	 */
	void enterDurationFieldNext(evaEnglishParser.DurationFieldNextContext ctx);
	/**
	 * Exit a parse tree produced by the {@code durationFieldNext}
	 * labeled alternative in {@link evaEnglishParser#durationField}.
	 * @param ctx the parse tree
	 */
	void exitDurationFieldNext(evaEnglishParser.DurationFieldNextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dateAdvDay}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDateAdvDay(evaEnglishParser.DateAdvDayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dateAdvDay}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDateAdvDay(evaEnglishParser.DateAdvDayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dateAdjNum}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDateAdjNum(evaEnglishParser.DateAdjNumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dateAdjNum}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDateAdjNum(evaEnglishParser.DateAdjNumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dateDefault}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDateDefault(evaEnglishParser.DateDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dateDefault}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDateDefault(evaEnglishParser.DateDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#dayOfMonth}.
	 * @param ctx the parse tree
	 */
	void enterDayOfMonth(evaEnglishParser.DayOfMonthContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#dayOfMonth}.
	 * @param ctx the parse tree
	 */
	void exitDayOfMonth(evaEnglishParser.DayOfMonthContext ctx);
	/**
	 * Enter a parse tree produced by the {@code timeAmPmHour}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTimeAmPmHour(evaEnglishParser.TimeAmPmHourContext ctx);
	/**
	 * Exit a parse tree produced by the {@code timeAmPmHour}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTimeAmPmHour(evaEnglishParser.TimeAmPmHourContext ctx);
	/**
	 * Enter a parse tree produced by the {@code timeAmPmDefault}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTimeAmPmDefault(evaEnglishParser.TimeAmPmDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code timeAmPmDefault}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTimeAmPmDefault(evaEnglishParser.TimeAmPmDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code timeComplete}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTimeComplete(evaEnglishParser.TimeCompleteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code timeComplete}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTimeComplete(evaEnglishParser.TimeCompleteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code timePartial}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTimePartial(evaEnglishParser.TimePartialContext ctx);
	/**
	 * Exit a parse tree produced by the {@code timePartial}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTimePartial(evaEnglishParser.TimePartialContext ctx);
	/**
	 * Enter a parse tree produced by the {@code timeTz}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTimeTz(evaEnglishParser.TimeTzContext ctx);
	/**
	 * Exit a parse tree produced by the {@code timeTz}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTimeTz(evaEnglishParser.TimeTzContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#timeZone}.
	 * @param ctx the parse tree
	 */
	void enterTimeZone(evaEnglishParser.TimeZoneContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#timeZone}.
	 * @param ctx the parse tree
	 */
	void exitTimeZone(evaEnglishParser.TimeZoneContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggFieldSum}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void enterAggFieldSum(evaEnglishParser.AggFieldSumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggFieldSum}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void exitAggFieldSum(evaEnglishParser.AggFieldSumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggFieldMax}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void enterAggFieldMax(evaEnglishParser.AggFieldMaxContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggFieldMax}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void exitAggFieldMax(evaEnglishParser.AggFieldMaxContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggFieldMin}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void enterAggFieldMin(evaEnglishParser.AggFieldMinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggFieldMin}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void exitAggFieldMin(evaEnglishParser.AggFieldMinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggFieldAvg}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void enterAggFieldAvg(evaEnglishParser.AggFieldAvgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggFieldAvg}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void exitAggFieldAvg(evaEnglishParser.AggFieldAvgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggFieldCount}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void enterAggFieldCount(evaEnglishParser.AggFieldCountContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggFieldCount}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 */
	void exitAggFieldCount(evaEnglishParser.AggFieldCountContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggNumberSum}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void enterAggNumberSum(evaEnglishParser.AggNumberSumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggNumberSum}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void exitAggNumberSum(evaEnglishParser.AggNumberSumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggNumberMax}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void enterAggNumberMax(evaEnglishParser.AggNumberMaxContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggNumberMax}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void exitAggNumberMax(evaEnglishParser.AggNumberMaxContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggNumberMin}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void enterAggNumberMin(evaEnglishParser.AggNumberMinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggNumberMin}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void exitAggNumberMin(evaEnglishParser.AggNumberMinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggNumberAvg}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void enterAggNumberAvg(evaEnglishParser.AggNumberAvgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggNumberAvg}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void exitAggNumberAvg(evaEnglishParser.AggNumberAvgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggNumberCount}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void enterAggNumberCount(evaEnglishParser.AggNumberCountContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggNumberCount}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 */
	void exitAggNumberCount(evaEnglishParser.AggNumberCountContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberAgg}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void enterNumberAgg(evaEnglishParser.NumberAggContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberAgg}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void exitNumberAgg(evaEnglishParser.NumberAggContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberT}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void enterNumberT(evaEnglishParser.NumberTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberT}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void exitNumberT(evaEnglishParser.NumberTContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberB}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void enterNumberB(evaEnglishParser.NumberBContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberB}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void exitNumberB(evaEnglishParser.NumberBContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberM}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void enterNumberM(evaEnglishParser.NumberMContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberM}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void exitNumberM(evaEnglishParser.NumberMContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberK}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void enterNumberK(evaEnglishParser.NumberKContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberK}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void exitNumberK(evaEnglishParser.NumberKContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberDefault}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void enterNumberDefault(evaEnglishParser.NumberDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberDefault}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 */
	void exitNumberDefault(evaEnglishParser.NumberDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldFirst}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldFirst(evaEnglishParser.RankFieldFirstContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldFirst}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldFirst(evaEnglishParser.RankFieldFirstContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldLast}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldLast(evaEnglishParser.RankFieldLastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldLast}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldLast(evaEnglishParser.RankFieldLastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldNthInverse}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldNthInverse(evaEnglishParser.RankFieldNthInverseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldNthInverse}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldNthInverse(evaEnglishParser.RankFieldNthInverseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldNth}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldNth(evaEnglishParser.RankFieldNthContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldNth}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldNth(evaEnglishParser.RankFieldNthContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldLeading}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldLeading(evaEnglishParser.RankFieldLeadingContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldLeading}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldLeading(evaEnglishParser.RankFieldLeadingContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldFollowing}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldFollowing(evaEnglishParser.RankFieldFollowingContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldFollowing}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldFollowing(evaEnglishParser.RankFieldFollowingContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rankFieldDefault}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void enterRankFieldDefault(evaEnglishParser.RankFieldDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rankFieldDefault}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 */
	void exitRankFieldDefault(evaEnglishParser.RankFieldDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code overClauseOrdPart}
	 * labeled alternative in {@link evaEnglishParser#overClause}.
	 * @param ctx the parse tree
	 */
	void enterOverClauseOrdPart(evaEnglishParser.OverClauseOrdPartContext ctx);
	/**
	 * Exit a parse tree produced by the {@code overClauseOrdPart}
	 * labeled alternative in {@link evaEnglishParser#overClause}.
	 * @param ctx the parse tree
	 */
	void exitOverClauseOrdPart(evaEnglishParser.OverClauseOrdPartContext ctx);
	/**
	 * Enter a parse tree produced by the {@code overClausePartOrd}
	 * labeled alternative in {@link evaEnglishParser#overClause}.
	 * @param ctx the parse tree
	 */
	void enterOverClausePartOrd(evaEnglishParser.OverClausePartOrdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code overClausePartOrd}
	 * labeled alternative in {@link evaEnglishParser#overClause}.
	 * @param ctx the parse tree
	 */
	void exitOverClausePartOrd(evaEnglishParser.OverClausePartOrdContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#functionTwoArgs}.
	 * @param ctx the parse tree
	 */
	void enterFunctionTwoArgs(evaEnglishParser.FunctionTwoArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#functionTwoArgs}.
	 * @param ctx the parse tree
	 */
	void exitFunctionTwoArgs(evaEnglishParser.FunctionTwoArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#functionOneArg}.
	 * @param ctx the parse tree
	 */
	void enterFunctionOneArg(evaEnglishParser.FunctionOneArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#functionOneArg}.
	 * @param ctx the parse tree
	 */
	void exitFunctionOneArg(evaEnglishParser.FunctionOneArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldOperationPlus}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldOperationPlus(evaEnglishParser.FieldOperationPlusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldOperationPlus}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldOperationPlus(evaEnglishParser.FieldOperationPlusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldAgg}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldAgg(evaEnglishParser.FieldAggContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldAgg}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldAgg(evaEnglishParser.FieldAggContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDefault}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldDefault(evaEnglishParser.FieldDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDefault}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldDefault(evaEnglishParser.FieldDefaultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldArticle}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldArticle(evaEnglishParser.FieldArticleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldArticle}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldArticle(evaEnglishParser.FieldArticleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDate}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldDate(evaEnglishParser.FieldDateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDate}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldDate(evaEnglishParser.FieldDateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldNumber}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldNumber(evaEnglishParser.FieldNumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldNumber}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldNumber(evaEnglishParser.FieldNumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldOneArgFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldOneArgFunction(evaEnglishParser.FieldOneArgFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldOneArgFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldOneArgFunction(evaEnglishParser.FieldOneArgFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldSubstrFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldSubstrFunction(evaEnglishParser.FieldSubstrFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldSubstrFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldSubstrFunction(evaEnglishParser.FieldSubstrFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDuration}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldDuration(evaEnglishParser.FieldDurationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDuration}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldDuration(evaEnglishParser.FieldDurationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDivFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldDivFunction(evaEnglishParser.FieldDivFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDivFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldDivFunction(evaEnglishParser.FieldDivFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldOperationMinus}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldOperationMinus(evaEnglishParser.FieldOperationMinusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldOperationMinus}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldOperationMinus(evaEnglishParser.FieldOperationMinusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldTwoArgFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldTwoArgFunction(evaEnglishParser.FieldTwoArgFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldTwoArgFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldTwoArgFunction(evaEnglishParser.FieldTwoArgFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldParen}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldParen(evaEnglishParser.FieldParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldParen}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldParen(evaEnglishParser.FieldParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldOperationOther}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldOperationOther(evaEnglishParser.FieldOperationOtherContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldOperationOther}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldOperationOther(evaEnglishParser.FieldOperationOtherContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldOperationDuration}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void enterFieldOperationDuration(evaEnglishParser.FieldOperationDurationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldOperationDuration}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 */
	void exitFieldOperationDuration(evaEnglishParser.FieldOperationDurationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldContentQualified}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 */
	void enterFieldContentQualified(evaEnglishParser.FieldContentQualifiedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldContentQualified}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 */
	void exitFieldContentQualified(evaEnglishParser.FieldContentQualifiedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldContentQuoted}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 */
	void enterFieldContentQuoted(evaEnglishParser.FieldContentQuotedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldContentQuoted}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 */
	void exitFieldContentQuoted(evaEnglishParser.FieldContentQuotedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldContentDefault}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 */
	void enterFieldContentDefault(evaEnglishParser.FieldContentDefaultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldContentDefault}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 */
	void exitFieldContentDefault(evaEnglishParser.FieldContentDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link evaEnglishParser#article}.
	 * @param ctx the parse tree
	 */
	void enterArticle(evaEnglishParser.ArticleContext ctx);
	/**
	 * Exit a parse tree produced by {@link evaEnglishParser#article}.
	 * @param ctx the parse tree
	 */
	void exitArticle(evaEnglishParser.ArticleContext ctx);
}