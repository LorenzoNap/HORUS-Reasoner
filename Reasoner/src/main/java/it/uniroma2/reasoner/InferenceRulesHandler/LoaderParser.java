package it.uniroma2.reasoner.InferenceRulesHandler;



import it.uniroma2.reasoner.domain.*;

import java.util.ArrayList;
import java.util.List;

import antr4.grammar.InferenceRulesGrammarBaseListener;
import antr4.grammar.InferenceRulesGrammarParser;
import antr4.grammar.InferenceRulesGrammarParser.Complex_conditionContext;
import antr4.grammar.InferenceRulesGrammarParser.ConclusionContext;
import antr4.grammar.InferenceRulesGrammarParser.New_ruleContext;
import antr4.grammar.InferenceRulesGrammarParser.PremiseContext;
import antr4.grammar.InferenceRulesGrammarParser.ValueContext;

/**
 * Get the parsed inference rule from parsing tree.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class LoaderParser extends InferenceRulesGrammarBaseListener{

	//List of inference rule
	private List<InferenceRule> inferenceRules = new ArrayList<InferenceRule>();
	
	
	public void exitParseInferenceRule(InferenceRulesGrammarParser.ParseInferenceRuleContext ctx) { 

		//For each rule on parsing tree, create a new InferenceRule object and populate it.
		for(New_ruleContext new_rule : ctx.new_rule()){
			//Create new inference rule
			InferenceRule newInferenceRule = new InferenceRule();
			//Save rule type
			newInferenceRule.setType(new_rule.rule_information().TYPE_RULE().getText());
			
			
			//Save inference rule name
			newInferenceRule.setInferenceRuleName(new_rule.rule_information().LetterAndSymbol().getText());
			//Save inference rule id
			newInferenceRule.setInferenceRuleID(Integer.parseInt(new_rule.rule_information().NUMBER().getText()));
			
			
			//Get all premisis of InferenceRule
			for(PremiseContext premise : new_rule.premise()){
				
			
				//Check if subject,predicate,object are var or iri or blank node.
				if(premise.triple().value().get(0) != null && premise.triple().value().get(1)!= null &&
						premise.triple().value().get(2) != null) {
				String subject = getItemValue(premise.triple().value().get(0));
				String predicate = getItemValue(premise.triple().value().get(1));
				String object = getItemValue(premise.triple().value().get(2));

				//Add premise to InferenceRule
				newInferenceRule.getPremisisTriple().add(new Triple(subject, predicate, object));
				}
			}

			if(newInferenceRule.getType().equals(InferenceRule.TPYE_INCONSISTENCY)){
				newInferenceRule.setInconsistency(true);
				newInferenceRule.setConclusionTriple(new ArrayList<Triple>());
			}
			
			else {
				
			//Get filter expression if exist
			if( new_rule.filter() != null &&  new_rule.filter().size() > 0){



				StringBuilder filter = new StringBuilder();
				if (new_rule.filter().get(0).condition().expression() != null){


					filter.append(getItemValue(new_rule.filter().get(0).condition().expression().value().get(0)));
					filter.append(new_rule.filter().get(0).condition().expression().LOGIC_OPERATOR());
					filter.append(getItemValue(new_rule.filter().get(0).condition().expression().value().get(1)));
				}
				else{
					for (Complex_conditionContext complex_conditionContext : new_rule.filter().get(0).condition().complex_condition()){

				   //LEFT CONDITION
					if(	complex_conditionContext.complex_expression_left().complex_expression().expression() != null){
						filter.append(getItemValue(complex_conditionContext.complex_expression_left().complex_expression().expression().value().get(0)));
						filter.append(complex_conditionContext.complex_expression_left().complex_expression().expression().LOGIC_OPERATOR().toString());
						filter.append(getItemValue(complex_conditionContext.complex_expression_left().complex_expression().expression().value().get(1)));
					}
					else{
						if(complex_conditionContext.complex_expression_left().complex_expression().expression_left() != null){
						filter.append("(");
						filter.append(getItemValue(complex_conditionContext.complex_expression_left().complex_expression().expression_left().expression().value().get(0)));
						filter.append(complex_conditionContext.complex_expression_left().complex_expression().expression_left().expression().LOGIC_OPERATOR().toString());
						filter.append(getItemValue(complex_conditionContext.complex_expression_left().complex_expression().expression_left().expression().value().get(1)));
						filter.append(complex_conditionContext.complex_expression_left().complex_expression().BOOLEAN());
						filter.append(getItemValue(complex_conditionContext.complex_expression_left().complex_expression().expression_right().expression().value().get(0)));
						filter.append(complex_conditionContext.complex_expression_left().complex_expression().expression_right().expression().LOGIC_OPERATOR());
						filter.append(getItemValue(complex_conditionContext.complex_expression_left().complex_expression().expression_right().expression().value().get(1)));
						filter.append(")");
						}
					}
					filter.append(complex_conditionContext.BOOLEAN().toString());	
					if(complex_conditionContext.complex_expression_right().complex_expression().expression() != null){
						if(	complex_conditionContext.complex_expression_right().complex_expression().expression() != null){
							filter.append(getItemValue(complex_conditionContext.complex_expression_right().complex_expression().expression().value().get(0)));
							filter.append(complex_conditionContext.complex_expression_right().complex_expression().expression().LOGIC_OPERATOR());
							filter.append(getItemValue(complex_conditionContext.complex_expression_right().complex_expression().expression().value().get(1)));
						}
						else{
							if(complex_conditionContext.complex_expression_right().complex_expression().expression_left() != null){
								filter.append("(");
							filter.append(getItemValue(complex_conditionContext.complex_expression_right().complex_expression().expression_left().expression().value().get(0)));
							filter.append(complex_conditionContext.complex_expression_right().complex_expression().expression_left().expression().LOGIC_OPERATOR());
							filter.append(getItemValue(complex_conditionContext.complex_expression_right().complex_expression().expression_left().expression().value().get(1)));
							filter.append(complex_conditionContext.complex_expression_right().complex_expression().BOOLEAN());
							filter.append(getItemValue(complex_conditionContext.complex_expression_right().complex_expression().expression_right().expression().value().get(0)));
							filter.append(complex_conditionContext.complex_expression_right().complex_expression().expression_right().expression().LOGIC_OPERATOR());
							filter.append(getItemValue(complex_conditionContext.complex_expression_right().complex_expression().expression_right().expression().value().get(1)));
							filter.append(")");
							
							}
						}
					}
					}
				}
				newInferenceRule.setFilterCondition(filter.toString());
			}
			//Get all premisis of InferenceRule
			for(ConclusionContext conclusion: new_rule.conclusion()){


				//Check if subject,predicate,object are var or iri or blank node.
				
				String subject = getItemValue(conclusion.triple().value().get(0));
				String predicate = getItemValue(conclusion.triple().value().get(1));
				String object = getItemValue(conclusion.triple().value().get(2));

				newInferenceRule.getConclusionTriple().add(new Triple(subject, predicate, object));
			}
			}

			inferenceRules.add(newInferenceRule);

		}
	}

	private String getItemValue(ValueContext valueContext){
		
		String itemValue = "";
		
		if(valueContext.var() != null){
			itemValue = valueContext.var().VAR1().getText();
		}
		if(valueContext.iri() != null){
			itemValue = valueContext.iri().IRIREF().getText();
		}
		if(valueContext.blankNode() != null){
			itemValue = valueContext.blankNode().BLANK_NODE_LABEL().getText();
		}
		if(valueContext.singleValue() != null){
			itemValue = valueContext.singleValue().getText();
		}
		
		return itemValue;
		
	}

	public List<InferenceRule> getInferenceRules() {
		if(inferenceRules == null){
			inferenceRules = new ArrayList<InferenceRule>();
		}
		return inferenceRules;
	}


	public void setInferenceRules(List<InferenceRule> inferenceRules) {
		this.inferenceRules = inferenceRules;
	}
	
	
}
