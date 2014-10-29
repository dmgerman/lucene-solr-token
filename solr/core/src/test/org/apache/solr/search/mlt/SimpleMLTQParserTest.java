begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.mlt
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|mlt
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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

begin_comment
comment|// TODO: Assert against expected parsed query for different min/maxidf values.
end_comment

begin_class
DECL|class|SimpleMLTQParserTest
specifier|public
class|class
name|SimpleMLTQParserTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|moreLikeThisBeforeClass
specifier|public
specifier|static
name|void
name|moreLikeThisBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
literal|"id"
decl_stmt|;
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"toyota"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"chevrolet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"suzuki"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ford"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ferrari"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"jaguar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"mclaren moon or the moon and moon moon shine "
operator|+
literal|"and the moon but moon was good foxes too"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"sonata"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick red fox jumped over the lazy big "
operator|+
literal|"and large brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The fat red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The slim red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped moon over the lazy "
operator|+
literal|"brown dogs moon. Of course moon. Foxes and moon come back to the foxes and moon"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The hose red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The court red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"22"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"23"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"24"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The file red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|id
argument_list|,
literal|"25"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"rod fix"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt}17"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='17']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='13']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='14']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='18']"
argument_list|,
literal|"//result/doc[5]/int[@name='id'][.='20']"
argument_list|,
literal|"//result/doc[6]/int[@name='id'][.='22']"
argument_list|,
literal|"//result/doc[7]/int[@name='id'][.='23']"
argument_list|,
literal|"//result/doc[8]/int[@name='id'][.='9']"
argument_list|,
literal|"//result/doc[9]/int[@name='id'][.='7']"
argument_list|,
literal|"//result/doc[10]/int[@name='id'][.='15']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

