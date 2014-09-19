begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.expression
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|expression
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ObjectArrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|SolrTestCaseJ4
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
name|analytics
operator|.
name|AbstractAnalyticsStatsTest
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|TrieDateField
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
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Scanner
import|;
end_import

begin_class
DECL|class|ExpressionTest
specifier|public
class|class
name|ExpressionTest
extends|extends
name|AbstractAnalyticsStatsTest
block|{
DECL|field|fileName
specifier|private
specifier|static
specifier|final
name|String
name|fileName
init|=
literal|"/analytics/requestFiles/expressions.txt"
decl_stmt|;
DECL|field|BASEPARMS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|BASEPARMS
init|=
operator|new
name|String
index|[]
block|{
literal|"q"
block|,
literal|"*:*"
block|,
literal|"indent"
block|,
literal|"true"
block|,
literal|"stats"
block|,
literal|"true"
block|,
literal|"olap"
block|,
literal|"true"
block|,
literal|"rows"
block|,
literal|"0"
block|}
decl_stmt|;
DECL|field|INT
specifier|private
specifier|static
specifier|final
name|int
name|INT
init|=
literal|71
decl_stmt|;
DECL|field|LONG
specifier|private
specifier|static
specifier|final
name|int
name|LONG
init|=
literal|36
decl_stmt|;
DECL|field|FLOAT
specifier|private
specifier|static
specifier|final
name|int
name|FLOAT
init|=
literal|93
decl_stmt|;
DECL|field|DOUBLE
specifier|private
specifier|static
specifier|final
name|int
name|DOUBLE
init|=
literal|49
decl_stmt|;
DECL|field|DATE
specifier|private
specifier|static
specifier|final
name|int
name|DATE
init|=
literal|12
decl_stmt|;
DECL|field|STRING
specifier|private
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|28
decl_stmt|;
DECL|field|NUM_LOOPS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_LOOPS
init|=
literal|100
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-analytics.xml"
argument_list|)
expr_stmt|;
name|h
operator|.
name|update
argument_list|(
literal|"<delete><query>*:*</query></delete>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_LOOPS
condition|;
operator|++
name|j
control|)
block|{
name|int
name|i
init|=
name|j
operator|%
name|INT
decl_stmt|;
name|long
name|l
init|=
name|j
operator|%
name|LONG
decl_stmt|;
name|float
name|f
init|=
name|j
operator|%
name|FLOAT
decl_stmt|;
name|double
name|d
init|=
name|j
operator|%
name|DOUBLE
decl_stmt|;
name|String
name|dt
init|=
operator|(
literal|1800
operator|+
name|j
operator|%
name|DATE
operator|)
operator|+
literal|"-12-31T23:59:59Z"
decl_stmt|;
name|String
name|s
init|=
literal|"str"
operator|+
operator|(
name|j
operator|%
name|STRING
operator|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
operator|+
name|j
argument_list|,
literal|"int_id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"long_ld"
argument_list|,
literal|""
operator|+
name|l
argument_list|,
literal|"float_fd"
argument_list|,
literal|""
operator|+
name|f
argument_list|,
literal|"double_dd"
argument_list|,
literal|""
operator|+
name|d
argument_list|,
literal|"date_dtd"
argument_list|,
name|dt
argument_list|,
literal|"string_sd"
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// to have several segments
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|setResponse
argument_list|(
name|h
operator|.
name|query
argument_list|(
name|request
argument_list|(
name|fileToStringArr
argument_list|(
name|ExpressionTest
operator|.
name|class
argument_list|,
name|fileName
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addTest
specifier|public
name|void
name|addTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|sumResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|uniqueResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"unique"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"su"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|sumResult
operator|+
name|uniqueResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|double
name|meanResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|medianResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"median"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|countResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"count"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"mcm"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|meanResult
operator|+
name|countResult
operator|+
name|medianResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|multiplyTest
specifier|public
name|void
name|multiplyTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|sumResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|uniqueResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"unique"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"su"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|sumResult
operator|*
name|uniqueResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|double
name|meanResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|medianResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"median"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|countResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"count"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"mcm"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|meanResult
operator|*
name|countResult
operator|*
name|medianResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|divideTest
specifier|public
name|void
name|divideTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|sumResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|uniqueResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"unique"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"su"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|sumResult
operator|/
name|uniqueResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|double
name|meanResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|countResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"count"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"mc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|meanResult
operator|/
name|countResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|powerTest
specifier|public
name|void
name|powerTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|sumResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|uniqueResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"unique"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"su"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|Math
operator|.
name|pow
argument_list|(
name|sumResult
argument_list|,
name|uniqueResult
argument_list|)
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|double
name|meanResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|countResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"count"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"mc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|Math
operator|.
name|pow
argument_list|(
name|meanResult
argument_list|,
name|countResult
argument_list|)
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|negateTest
specifier|public
name|void
name|negateTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|sumResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"s"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
operator|-
literal|1
operator|*
name|sumResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|double
name|countResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"count"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"c"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
operator|-
literal|1
operator|*
name|countResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|absoluteValueTest
specifier|public
name|void
name|absoluteValueTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|sumResult
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"s"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|sumResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|double
name|countResult
init|=
operator|(
operator|(
name|Long
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"count"
argument_list|,
name|VAL_TYPE
operator|.
name|LONG
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"c"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|countResult
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|constantNumberTest
specifier|public
name|void
name|constantNumberTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"cnr"
argument_list|,
literal|"c8"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
literal|8
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"cnr"
argument_list|,
literal|"c10"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
literal|10
argument_list|,
name|result
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|dateMathTest
specifier|public
name|void
name|dateMathTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|math
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"cme"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|DateMathParser
name|date
init|=
operator|new
name|DateMathParser
argument_list|()
decl_stmt|;
name|date
operator|.
name|setNow
argument_list|(
name|TrieDateField
operator|.
name|parseDate
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"median"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|dateMath
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"dmme"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|TrieDateField
operator|.
name|parseDate
argument_list|(
name|dateMath
argument_list|)
argument_list|,
name|date
operator|.
name|parseMath
argument_list|(
name|math
argument_list|)
argument_list|)
expr_stmt|;
name|math
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"cma"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|date
operator|=
operator|new
name|DateMathParser
argument_list|()
expr_stmt|;
name|date
operator|.
name|setNow
argument_list|(
name|TrieDateField
operator|.
name|parseDate
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateMath
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"dmma"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|TrieDateField
operator|.
name|parseDate
argument_list|(
name|dateMath
argument_list|)
argument_list|,
name|date
operator|.
name|parseMath
argument_list|(
name|math
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|constantDateTest
specifier|public
name|void
name|constantDateTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|date
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"cd1"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|String
name|str
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"cs1"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|date
argument_list|,
name|str
argument_list|)
expr_stmt|;
name|date
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"cd2"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"cs2"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|date
argument_list|,
name|str
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|constantStringTest
specifier|public
name|void
name|constantStringTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"cs1"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|str
argument_list|,
literal|"this is the first"
argument_list|)
expr_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"cs2"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|str
argument_list|,
literal|"this is the second"
argument_list|)
expr_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"cs3"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|str
argument_list|,
literal|"this is the third"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|concatenateTest
specifier|public
name|void
name|concatenateTest
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"csmin"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"min"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|concat
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"ccmin"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|concat
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"csmax"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|concat
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"ccmax"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|concat
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reverseTest
specifier|public
name|void
name|reverseTest
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"min"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"rmin"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|rev
argument_list|,
name|builder
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|rev
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"rmax"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|rev
argument_list|,
name|builder
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|request
specifier|public
specifier|static
name|SolrQueryRequest
name|request
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|SolrTestCaseJ4
operator|.
name|req
argument_list|(
name|ObjectArrays
operator|.
name|concat
argument_list|(
name|BASEPARMS
argument_list|,
name|args
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fileToStringArr
specifier|public
specifier|static
name|String
index|[]
name|fileToStringArr
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|InputStream
name|in
init|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Resource not found: "
operator|+
name|fileName
argument_list|)
throw|;
name|Scanner
name|file
init|=
operator|new
name|Scanner
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|strList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|file
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|String
name|line
init|=
name|file
operator|.
name|nextLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|<
literal|2
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|param
init|=
name|line
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|strList
operator|.
name|add
argument_list|(
name|param
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|strList
operator|.
name|add
argument_list|(
name|param
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|strList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|file
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
