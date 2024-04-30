// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/evaEnglish.g4 by ANTLR 4.7
package nlp.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link evaEnglishParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface evaEnglishVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code commandSelect}
	 * labeled alternative in {@link evaEnglishParser#question}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommandSelect(evaEnglishParser.CommandSelectContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#selectClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectClause(evaEnglishParser.SelectClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#fieldList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldList(evaEnglishParser.FieldListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaParen}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaParen(evaEnglishParser.CriteriaParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaNegative}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaNegative(evaEnglishParser.CriteriaNegativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaPositive}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaPositive(evaEnglishParser.CriteriaPositiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaWhereDefault}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaWhereDefault(evaEnglishParser.CriteriaWhereDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaAnd}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaAnd(evaEnglishParser.CriteriaAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaWhereOrphanNegative}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaWhereOrphanNegative(evaEnglishParser.CriteriaWhereOrphanNegativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaWhereOrphanDefault}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaWhereOrphanDefault(evaEnglishParser.CriteriaWhereOrphanDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaOr}
	 * labeled alternative in {@link evaEnglishParser#criteriaClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaOr(evaEnglishParser.CriteriaOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereOrphanBetween}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereOrphanBetween(evaEnglishParser.WhereOrphanBetweenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereOrphanGt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereOrphanGt(evaEnglishParser.WhereOrphanGtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereOrphanLt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereOrphanLt(evaEnglishParser.WhereOrphanLtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereOrphanGtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereOrphanGtEq(evaEnglishParser.WhereOrphanGtEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereOrphanLtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereOrphanLtEq(evaEnglishParser.WhereOrphanLtEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereOrphanEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseOrphanContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereOrphanEq(evaEnglishParser.WhereOrphanEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateFieldDateTime}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateFieldDateTime(evaEnglishParser.DateFieldDateTimeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateFieldDateOnly}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateFieldDateOnly(evaEnglishParser.DateFieldDateOnlyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateFieldTimeOnly}
	 * labeled alternative in {@link evaEnglishParser#dateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateFieldTimeOnly(evaEnglishParser.DateFieldTimeOnlyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereBetween}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereBetween(evaEnglishParser.WhereBetweenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereGtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereGtEq(evaEnglishParser.WhereGtEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereLtEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereLtEq(evaEnglishParser.WhereLtEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereGt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereGt(evaEnglishParser.WhereGtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereLt}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereLt(evaEnglishParser.WhereLtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereDuration}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereDuration(evaEnglishParser.WhereDurationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereDateEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereDateEq(evaEnglishParser.WhereDateEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereStringFunctions}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereStringFunctions(evaEnglishParser.WhereStringFunctionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereDefault}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereDefault(evaEnglishParser.WhereDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereDefaultNegative}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereDefaultNegative(evaEnglishParser.WhereDefaultNegativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereEq}
	 * labeled alternative in {@link evaEnglishParser#whereClauseContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereEq(evaEnglishParser.WhereEqContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#negativeVerb}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegativeVerb(evaEnglishParser.NegativeVerbContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#positiveVerb}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositiveVerb(evaEnglishParser.PositiveVerbContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#comparisionPhrases}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisionPhrases(evaEnglishParser.ComparisionPhrasesContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#unavailablePhrases}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnavailablePhrases(evaEnglishParser.UnavailablePhrasesContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#duration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDuration(evaEnglishParser.DurationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code durationFieldPast}
	 * labeled alternative in {@link evaEnglishParser#durationField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDurationFieldPast(evaEnglishParser.DurationFieldPastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code durationFieldNext}
	 * labeled alternative in {@link evaEnglishParser#durationField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDurationFieldNext(evaEnglishParser.DurationFieldNextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateAdvDay}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateAdvDay(evaEnglishParser.DateAdvDayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateAdjNum}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateAdjNum(evaEnglishParser.DateAdjNumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateDefault}
	 * labeled alternative in {@link evaEnglishParser#date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateDefault(evaEnglishParser.DateDefaultContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#dayOfMonth}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDayOfMonth(evaEnglishParser.DayOfMonthContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timeAmPmHour}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeAmPmHour(evaEnglishParser.TimeAmPmHourContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timeAmPmDefault}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeAmPmDefault(evaEnglishParser.TimeAmPmDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timeComplete}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeComplete(evaEnglishParser.TimeCompleteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timePartial}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimePartial(evaEnglishParser.TimePartialContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timeTz}
	 * labeled alternative in {@link evaEnglishParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeTz(evaEnglishParser.TimeTzContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#timeZone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeZone(evaEnglishParser.TimeZoneContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggFieldSum}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggFieldSum(evaEnglishParser.AggFieldSumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggFieldMax}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggFieldMax(evaEnglishParser.AggFieldMaxContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggFieldMin}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggFieldMin(evaEnglishParser.AggFieldMinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggFieldAvg}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggFieldAvg(evaEnglishParser.AggFieldAvgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggFieldCount}
	 * labeled alternative in {@link evaEnglishParser#aggregateField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggFieldCount(evaEnglishParser.AggFieldCountContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggNumberSum}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggNumberSum(evaEnglishParser.AggNumberSumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggNumberMax}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggNumberMax(evaEnglishParser.AggNumberMaxContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggNumberMin}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggNumberMin(evaEnglishParser.AggNumberMinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggNumberAvg}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggNumberAvg(evaEnglishParser.AggNumberAvgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggNumberCount}
	 * labeled alternative in {@link evaEnglishParser#aggregateNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggNumberCount(evaEnglishParser.AggNumberCountContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberAgg}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberAgg(evaEnglishParser.NumberAggContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberT}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberT(evaEnglishParser.NumberTContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberB}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberB(evaEnglishParser.NumberBContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberM}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberM(evaEnglishParser.NumberMContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberK}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberK(evaEnglishParser.NumberKContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberDefault}
	 * labeled alternative in {@link evaEnglishParser#numberField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberDefault(evaEnglishParser.NumberDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldFirst}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldFirst(evaEnglishParser.RankFieldFirstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldLast}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldLast(evaEnglishParser.RankFieldLastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldNthInverse}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldNthInverse(evaEnglishParser.RankFieldNthInverseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldNth}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldNth(evaEnglishParser.RankFieldNthContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldLeading}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldLeading(evaEnglishParser.RankFieldLeadingContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldFollowing}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldFollowing(evaEnglishParser.RankFieldFollowingContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankFieldDefault}
	 * labeled alternative in {@link evaEnglishParser#rankField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankFieldDefault(evaEnglishParser.RankFieldDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code overClauseOrdPart}
	 * labeled alternative in {@link evaEnglishParser#overClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOverClauseOrdPart(evaEnglishParser.OverClauseOrdPartContext ctx);
	/**
	 * Visit a parse tree produced by the {@code overClausePartOrd}
	 * labeled alternative in {@link evaEnglishParser#overClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOverClausePartOrd(evaEnglishParser.OverClausePartOrdContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#functionTwoArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionTwoArgs(evaEnglishParser.FunctionTwoArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#functionOneArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionOneArg(evaEnglishParser.FunctionOneArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldOperationPlus}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldOperationPlus(evaEnglishParser.FieldOperationPlusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldAgg}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldAgg(evaEnglishParser.FieldAggContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldDefault}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDefault(evaEnglishParser.FieldDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldArticle}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldArticle(evaEnglishParser.FieldArticleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldDate}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDate(evaEnglishParser.FieldDateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldNumber}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldNumber(evaEnglishParser.FieldNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldOneArgFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldOneArgFunction(evaEnglishParser.FieldOneArgFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldSubstrFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldSubstrFunction(evaEnglishParser.FieldSubstrFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldDuration}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDuration(evaEnglishParser.FieldDurationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldDivFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDivFunction(evaEnglishParser.FieldDivFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldOperationMinus}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldOperationMinus(evaEnglishParser.FieldOperationMinusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldTwoArgFunction}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldTwoArgFunction(evaEnglishParser.FieldTwoArgFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldParen}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldParen(evaEnglishParser.FieldParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldOperationOther}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldOperationOther(evaEnglishParser.FieldOperationOtherContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldOperationDuration}
	 * labeled alternative in {@link evaEnglishParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldOperationDuration(evaEnglishParser.FieldOperationDurationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldContentQualified}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldContentQualified(evaEnglishParser.FieldContentQualifiedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldContentQuoted}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldContentQuoted(evaEnglishParser.FieldContentQuotedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldContentDefault}
	 * labeled alternative in {@link evaEnglishParser#fieldContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldContentDefault(evaEnglishParser.FieldContentDefaultContext ctx);
	/**
	 * Visit a parse tree produced by {@link evaEnglishParser#article}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArticle(evaEnglishParser.ArticleContext ctx);
}