begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.eval
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
name|eval
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math3
operator|.
name|random
operator|.
name|EmpiricalDistribution
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math3
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|StatisticalSummary
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
name|StreamFactory
import|;
end_import

begin_class
DECL|class|EmpiricalDistributionEvaluator
specifier|public
class|class
name|EmpiricalDistributionEvaluator
extends|extends
name|ComplexEvaluator
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
DECL|method|EmpiricalDistributionEvaluator
specifier|public
name|EmpiricalDistributionEvaluator
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
name|super
argument_list|(
name|expression
argument_list|,
name|factory
argument_list|)
expr_stmt|;
if|if
condition|(
literal|1
operator|!=
name|subEvaluators
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
literal|"Invalid expression %s - expecting one column but found %d"
argument_list|,
name|expression
argument_list|,
name|subEvaluators
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|evaluate
specifier|public
name|Tuple
name|evaluate
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|colEval1
init|=
name|subEvaluators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Number
argument_list|>
name|numbers1
init|=
operator|(
name|List
argument_list|<
name|Number
argument_list|>
operator|)
name|colEval1
operator|.
name|evaluate
argument_list|(
name|tuple
argument_list|)
decl_stmt|;
name|double
index|[]
name|column1
init|=
operator|new
name|double
index|[
name|numbers1
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numbers1
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|column1
index|[
name|i
index|]
operator|=
name|numbers1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|column1
argument_list|)
expr_stmt|;
name|EmpiricalDistribution
name|empiricalDistribution
init|=
operator|new
name|EmpiricalDistribution
argument_list|()
decl_stmt|;
name|empiricalDistribution
operator|.
name|load
argument_list|(
name|column1
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|StatisticalSummary
name|statisticalSummary
init|=
name|empiricalDistribution
operator|.
name|getSampleStats
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"max"
argument_list|,
name|statisticalSummary
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"mean"
argument_list|,
name|statisticalSummary
operator|.
name|getMean
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"min"
argument_list|,
name|statisticalSummary
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"stdev"
argument_list|,
name|statisticalSummary
operator|.
name|getStandardDeviation
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"sum"
argument_list|,
name|statisticalSummary
operator|.
name|getSum
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"N"
argument_list|,
name|statisticalSummary
operator|.
name|getN
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"var"
argument_list|,
name|statisticalSummary
operator|.
name|getVariance
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|EmpiricalDistributionTuple
argument_list|(
name|empiricalDistribution
argument_list|,
name|column1
argument_list|,
name|map
argument_list|)
return|;
block|}
DECL|class|EmpiricalDistributionTuple
specifier|public
specifier|static
class|class
name|EmpiricalDistributionTuple
extends|extends
name|Tuple
block|{
DECL|field|empiricalDistribution
specifier|private
name|EmpiricalDistribution
name|empiricalDistribution
decl_stmt|;
DECL|field|backingArray
specifier|private
name|double
index|[]
name|backingArray
decl_stmt|;
DECL|method|EmpiricalDistributionTuple
specifier|public
name|EmpiricalDistributionTuple
parameter_list|(
name|EmpiricalDistribution
name|empiricalDistribution
parameter_list|,
name|double
index|[]
name|backingArray
parameter_list|,
name|Map
name|map
parameter_list|)
block|{
name|super
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|this
operator|.
name|empiricalDistribution
operator|=
name|empiricalDistribution
expr_stmt|;
name|this
operator|.
name|backingArray
operator|=
name|backingArray
expr_stmt|;
block|}
DECL|method|percentile
specifier|public
name|double
name|percentile
parameter_list|(
name|double
name|d
parameter_list|)
block|{
name|int
name|slot
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|backingArray
argument_list|,
name|d
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|==
literal|0
condition|)
block|{
return|return
literal|0.0
return|;
block|}
if|if
condition|(
name|slot
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|slot
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|0.0D
return|;
block|}
else|else
block|{
comment|//Not a direct hit
name|slot
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|slot
argument_list|)
expr_stmt|;
operator|--
name|slot
expr_stmt|;
if|if
condition|(
name|slot
operator|==
name|backingArray
operator|.
name|length
condition|)
block|{
return|return
literal|1.0D
return|;
block|}
else|else
block|{
return|return
operator|(
name|this
operator|.
name|empiricalDistribution
operator|.
name|cumulativeProbability
argument_list|(
name|backingArray
index|[
name|slot
index|]
argument_list|)
operator|)
return|;
block|}
block|}
block|}
else|else
block|{
return|return
name|this
operator|.
name|empiricalDistribution
operator|.
name|cumulativeProbability
argument_list|(
name|backingArray
index|[
name|slot
index|]
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
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
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
return|return
operator|new
name|Explanation
argument_list|(
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|EVALUATOR
argument_list|)
operator|.
name|withFunctionName
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

