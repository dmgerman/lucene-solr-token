begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|FieldComparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|StreamComparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|FieldEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|StreamEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
operator|.
name|ExpressionType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import

begin_class
DECL|class|CartesianProductStream
specifier|public
class|class
name|CartesianProductStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|stream
specifier|private
name|TupleStream
name|stream
decl_stmt|;
DECL|field|evaluators
specifier|private
name|List
argument_list|<
name|NamedEvaluator
argument_list|>
name|evaluators
decl_stmt|;
DECL|field|orderBy
specifier|private
name|StreamComparator
name|orderBy
decl_stmt|;
comment|// Used to contain the sorted queue of generated tuples
DECL|field|generatedTuples
specifier|private
name|LinkedList
argument_list|<
name|Tuple
argument_list|>
name|generatedTuples
decl_stmt|;
DECL|method|CartesianProductStream
specifier|public
name|CartesianProductStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|functionName
init|=
name|factory
operator|.
name|getFunctionName
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|// grab all parameters out
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|streamExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|Expressible
operator|.
name|class
argument_list|,
name|TupleStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpressionParameter
argument_list|>
name|evaluateAsExpressions
init|=
name|factory
operator|.
name|getOperandsOfType
argument_list|(
name|expression
argument_list|,
name|StreamExpressionValue
operator|.
name|class
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|orderByExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"productSort"
argument_list|)
decl_stmt|;
comment|// validate expression contains only what we want.
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|streamExpressions
operator|.
name|size
argument_list|()
operator|+
name|evaluateAsExpressions
operator|.
name|size
argument_list|()
operator|+
operator|(
literal|null
operator|==
name|orderByExpression
condition|?
literal|0
else|:
literal|1
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid %s expression %s - unknown operands found"
argument_list|,
name|functionName
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|!=
name|streamExpressions
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid %s expression %s - expecting single stream but found %d (must be TupleStream types)"
argument_list|,
name|functionName
argument_list|,
name|expression
argument_list|,
name|streamExpressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|stream
operator|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|orderBy
operator|=
literal|null
operator|==
name|orderByExpression
condition|?
literal|null
else|:
name|factory
operator|.
name|constructComparator
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|orderByExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldComparator
operator|.
name|class
argument_list|)
expr_stmt|;
name|evaluators
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|StreamExpressionParameter
name|evaluateAsExpression
range|:
name|evaluateAsExpressions
control|)
block|{
name|String
name|fullString
init|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|evaluateAsExpression
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|originalFullString
init|=
name|fullString
decl_stmt|;
comment|// used for error messages
comment|// remove possible wrapping quotes
if|if
condition|(
name|fullString
operator|.
name|length
argument_list|()
operator|>
literal|2
operator|&&
name|fullString
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|&&
name|fullString
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
name|fullString
operator|=
name|fullString
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|fullString
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|String
name|evaluatorPart
init|=
literal|null
decl_stmt|;
name|String
name|asNamePart
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fullString
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|" as "
argument_list|)
condition|)
block|{
name|String
index|[]
name|parts
init|=
name|fullString
operator|.
name|split
argument_list|(
literal|"(?i) as "
argument_list|)
decl_stmt|;
comment|// ensure we are splitting in a case-insensitive way
if|if
condition|(
literal|2
operator|!=
name|parts
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid %s expression %s - expecting evaluator of form 'fieldA' or 'fieldA as alias' but found %s"
argument_list|,
name|functionName
argument_list|,
name|expression
argument_list|,
name|originalFullString
argument_list|)
argument_list|)
throw|;
block|}
name|evaluatorPart
operator|=
name|parts
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
name|asNamePart
operator|=
name|parts
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|evaluatorPart
operator|=
name|fullString
expr_stmt|;
comment|// no rename
block|}
name|boolean
name|wasHandledAsEvaluatorFunction
init|=
literal|false
decl_stmt|;
name|StreamEvaluator
name|evaluator
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|evaluatorPart
operator|.
name|contains
argument_list|(
literal|"("
argument_list|)
condition|)
block|{
comment|// is a possible evaluator
try|try
block|{
name|StreamExpression
name|asValueExpression
init|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
name|evaluatorPart
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|.
name|doesRepresentTypes
argument_list|(
name|asValueExpression
argument_list|,
name|StreamEvaluator
operator|.
name|class
argument_list|)
condition|)
block|{
name|evaluator
operator|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
name|asValueExpression
argument_list|)
expr_stmt|;
name|wasHandledAsEvaluatorFunction
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// it was not handled, so treat as a non-evaluator
block|}
block|}
if|if
condition|(
operator|!
name|wasHandledAsEvaluatorFunction
condition|)
block|{
comment|// treat as a straight field evaluator
name|evaluator
operator|=
operator|new
name|FieldEvaluator
argument_list|(
name|evaluatorPart
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|asNamePart
condition|)
block|{
name|asNamePart
operator|=
name|evaluatorPart
expr_stmt|;
comment|// just use the field name
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|evaluator
operator|||
literal|null
operator|==
name|asNamePart
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid %s expression %s - failed to parse evaluator '%s'"
argument_list|,
name|functionName
argument_list|,
name|expression
argument_list|,
name|originalFullString
argument_list|)
argument_list|)
throw|;
block|}
name|evaluators
operator|.
name|add
argument_list|(
operator|new
name|NamedEvaluator
argument_list|(
name|asNamePart
argument_list|,
name|evaluator
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|toExpression
specifier|private
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|,
name|boolean
name|includeStreams
parameter_list|)
throws|throws
name|IOException
block|{
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeStreams
condition|)
block|{
comment|// we know stream is expressible
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|stream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|.
name|addParameter
argument_list|(
literal|"<stream>"
argument_list|)
expr_stmt|;
block|}
comment|// selected evaluators
for|for
control|(
name|NamedEvaluator
name|evaluator
range|:
name|evaluators
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s as %s"
argument_list|,
name|evaluator
operator|.
name|getEvaluator
argument_list|()
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|,
name|evaluator
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"productSort"
argument_list|,
name|orderBy
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|expression
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|explanation
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withChildren
argument_list|(
operator|new
name|Explanation
index|[]
block|{
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
block|}
argument_list|)
operator|.
name|withFunctionName
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|NamedEvaluator
name|evaluator
range|:
name|evaluators
control|)
block|{
name|explanation
operator|.
name|addHelper
argument_list|(
name|evaluator
operator|.
name|getEvaluator
argument_list|()
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|explanation
operator|.
name|addHelper
argument_list|(
name|orderBy
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|explanation
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|generatedTuples
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Tuple
name|tuple
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
return|return
name|tuple
return|;
block|}
comment|// returns tuples in desired sorted order
name|generatedTuples
operator|=
name|generateTupleList
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
return|return
name|generatedTuples
operator|.
name|pop
argument_list|()
return|;
block|}
DECL|method|generateTupleList
specifier|private
name|LinkedList
argument_list|<
name|Tuple
argument_list|>
name|generateTupleList
parameter_list|(
name|Tuple
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|evaluatedValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|NamedEvaluator
name|evaluator
range|:
name|evaluators
control|)
block|{
name|evaluatedValues
operator|.
name|put
argument_list|(
name|evaluator
operator|.
name|getName
argument_list|()
argument_list|,
name|evaluator
operator|.
name|getEvaluator
argument_list|()
operator|.
name|evaluate
argument_list|(
name|original
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// use an array list internally because it has better sort performance
comment|// in Java 8. We do pay a conversion to a linked list but ..... oh well
name|ArrayList
argument_list|<
name|Tuple
argument_list|>
name|generatedTupleList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
index|[]
name|workingIndexes
init|=
operator|new
name|int
index|[
name|evaluators
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// java language spec ensures all values are 0
do|do
block|{
name|Tuple
name|generated
init|=
name|original
operator|.
name|clone
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|offset
init|=
literal|0
init|;
name|offset
operator|<
name|workingIndexes
operator|.
name|length
condition|;
operator|++
name|offset
control|)
block|{
name|String
name|fieldName
init|=
name|evaluators
operator|.
name|get
argument_list|(
name|offset
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Object
name|evaluatedValue
init|=
name|evaluatedValues
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|evaluatedValue
operator|instanceof
name|Collection
condition|)
block|{
comment|// because of the way a FieldEvaluator works we know that
comment|// any collection is a list.
name|generated
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|evaluatedValue
operator|)
operator|.
name|get
argument_list|(
name|workingIndexes
index|[
name|offset
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|generatedTupleList
operator|.
name|add
argument_list|(
name|generated
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|iterate
argument_list|(
name|evaluators
argument_list|,
name|workingIndexes
argument_list|,
name|evaluatedValues
argument_list|)
condition|)
do|;
comment|// order if we need to
if|if
condition|(
literal|null
operator|!=
name|orderBy
condition|)
block|{
name|generatedTupleList
operator|.
name|sort
argument_list|(
name|orderBy
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LinkedList
argument_list|<>
argument_list|(
name|generatedTupleList
argument_list|)
return|;
block|}
DECL|method|iterate
specifier|private
name|boolean
name|iterate
parameter_list|(
name|List
argument_list|<
name|NamedEvaluator
argument_list|>
name|evaluators
parameter_list|,
name|int
index|[]
name|indexes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|evaluatedValues
parameter_list|)
block|{
comment|// this assumes evaluators and indexes are the same length, which is ok cause we created it so we know it is
comment|// go right to left and increment, returning true if we're not at the end
for|for
control|(
name|int
name|offset
init|=
name|indexes
operator|.
name|length
operator|-
literal|1
init|;
name|offset
operator|>=
literal|0
condition|;
operator|--
name|offset
control|)
block|{
name|Object
name|evaluatedValue
init|=
name|evaluatedValues
operator|.
name|get
argument_list|(
name|evaluators
operator|.
name|get
argument_list|(
name|offset
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|evaluatedValue
operator|instanceof
name|Collection
condition|)
block|{
name|int
name|currentIndexValue
init|=
name|indexes
index|[
name|offset
index|]
decl_stmt|;
if|if
condition|(
name|currentIndexValue
operator|<
operator|(
operator|(
name|Collection
operator|)
name|evaluatedValue
operator|)
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|indexes
index|[
name|offset
index|]
operator|=
name|currentIndexValue
operator|+
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
literal|0
operator|!=
name|offset
condition|)
block|{
name|indexes
index|[
name|offset
index|]
operator|=
literal|0
expr_stmt|;
comment|// move to the left
block|}
block|}
block|}
comment|// no more
return|return
literal|false
return|;
block|}
comment|/** Return the incoming sort + the sort applied to the generated tuples */
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|orderBy
condition|)
block|{
return|return
name|stream
operator|.
name|getStreamSort
argument_list|()
operator|.
name|append
argument_list|(
name|orderBy
argument_list|)
return|;
block|}
return|return
name|stream
operator|.
name|getStreamSort
argument_list|()
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
for|for
control|(
name|NamedEvaluator
name|evaluator
range|:
name|evaluators
control|)
block|{
name|evaluator
operator|.
name|getEvaluator
argument_list|()
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
name|List
argument_list|<
name|TupleStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
name|generatedTuples
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|generatedTuples
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|class|NamedEvaluator
class|class
name|NamedEvaluator
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|evaluator
specifier|private
name|StreamEvaluator
name|evaluator
decl_stmt|;
DECL|method|NamedEvaluator
specifier|public
name|NamedEvaluator
parameter_list|(
name|String
name|name
parameter_list|,
name|StreamEvaluator
name|evaluator
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|evaluator
operator|=
name|evaluator
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getEvaluator
specifier|public
name|StreamEvaluator
name|getEvaluator
parameter_list|()
block|{
return|return
name|evaluator
return|;
block|}
block|}
block|}
end_class

end_unit
