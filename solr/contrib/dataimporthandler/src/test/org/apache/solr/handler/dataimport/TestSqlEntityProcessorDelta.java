begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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
name|LocalSolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Test with various combinations of parameters, child entites, transformers.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Investigate failures on Policeman Jenkins Linux"
argument_list|)
DECL|class|TestSqlEntityProcessorDelta
specifier|public
class|class
name|TestSqlEntityProcessorDelta
extends|extends
name|AbstractDIHJdbcTestCase
block|{
DECL|field|delta
specifier|private
name|boolean
name|delta
init|=
literal|false
decl_stmt|;
DECL|field|useParentDeltaQueryParam
specifier|private
name|boolean
name|useParentDeltaQueryParam
init|=
literal|false
decl_stmt|;
DECL|field|personChanges
specifier|private
name|IntChanges
name|personChanges
init|=
literal|null
decl_stmt|;
DECL|field|countryChanges
specifier|private
name|String
index|[]
name|countryChanges
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setupDeltaTest
specifier|public
name|void
name|setupDeltaTest
parameter_list|()
block|{
name|delta
operator|=
literal|false
expr_stmt|;
name|personChanges
operator|=
literal|null
expr_stmt|;
name|countryChanges
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleEntity
specifier|public
name|void
name|testSingleEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|singleEntity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|changeStuff
argument_list|()
expr_stmt|;
name|int
name|c
init|=
name|calculateDatabaseCalls
argument_list|()
decl_stmt|;
name|singleEntity
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|validateChanges
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithSimpleTransformer
specifier|public
name|void
name|testWithSimpleTransformer
parameter_list|()
throws|throws
name|Exception
block|{
name|simpleTransform
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|changeStuff
argument_list|()
expr_stmt|;
name|simpleTransform
argument_list|(
name|calculateDatabaseCalls
argument_list|()
argument_list|)
expr_stmt|;
name|validateChanges
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithComplexTransformer
specifier|public
name|void
name|testWithComplexTransformer
parameter_list|()
throws|throws
name|Exception
block|{
name|complexTransform
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|changeStuff
argument_list|()
expr_stmt|;
name|complexTransform
argument_list|(
name|calculateDatabaseCalls
argument_list|()
argument_list|,
name|personChanges
operator|.
name|deletedKeys
operator|.
name|length
argument_list|)
expr_stmt|;
name|validateChanges
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChildEntities
specifier|public
name|void
name|testChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|useParentDeltaQueryParam
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|withChildEntities
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|changeStuff
argument_list|()
expr_stmt|;
name|withChildEntities
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|validateChanges
argument_list|()
expr_stmt|;
block|}
DECL|method|calculateDatabaseCalls
specifier|private
name|int
name|calculateDatabaseCalls
parameter_list|()
block|{
comment|//The main query generates 1
comment|//Deletes generate 1
comment|//Each add/mod generate 1
name|int
name|c
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|countryChanges
operator|!=
literal|null
condition|)
block|{
name|c
operator|+=
name|countryChanges
operator|.
name|length
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|personChanges
operator|!=
literal|null
condition|)
block|{
name|c
operator|+=
name|personChanges
operator|.
name|addedKeys
operator|.
name|length
operator|+
name|personChanges
operator|.
name|changedKeys
operator|.
name|length
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
DECL|method|validateChanges
specifier|private
name|void
name|validateChanges
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|personChanges
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|id
range|:
name|personChanges
operator|.
name|addedKeys
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:"
operator|+
name|id
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|id
range|:
name|personChanges
operator|.
name|deletedKeys
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:"
operator|+
name|id
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|id
range|:
name|personChanges
operator|.
name|changedKeys
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:"
operator|+
name|id
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"substring(//doc/arr[@name='NAME_mult_s']/str[1], 1, 8)='MODIFIED'"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|countryChanges
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|code
range|:
name|countryChanges
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"COUNTRY_CODE_s:"
operator|+
name|code
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
name|numberPeopleByCountryCode
argument_list|(
name|code
argument_list|)
operator|+
literal|"']"
argument_list|,
literal|"substring(//doc/str[@name='COUNTRY_NAME_s'], 1, 8)='MODIFIED'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|changeStuff
specifier|private
name|void
name|changeStuff
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|countryEntity
condition|)
block|{
name|int
name|n
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|n
condition|)
block|{
case|case
literal|0
case|:
name|personChanges
operator|=
name|modifySomePeople
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|countryChanges
operator|=
name|modifySomeCountries
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|personChanges
operator|=
name|modifySomePeople
argument_list|()
expr_stmt|;
name|countryChanges
operator|=
name|modifySomeCountries
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|personChanges
operator|=
name|modifySomePeople
argument_list|()
expr_stmt|;
block|}
name|delta
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|generateRequest
specifier|protected
name|LocalSolrQueryRequest
name|generateRequest
parameter_list|()
block|{
return|return
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
operator|(
name|delta
condition|?
literal|"delta-import"
else|:
literal|"full-import"
operator|)
argument_list|,
literal|"dataConfig"
argument_list|,
name|generateConfig
argument_list|()
argument_list|,
literal|"clean"
argument_list|,
operator|(
name|delta
condition|?
literal|"false"
else|:
literal|"true"
operator|)
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"synchronous"
argument_list|,
literal|"true"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
return|;
block|}
DECL|method|deltaQueriesPersonTable
specifier|protected
name|String
name|deltaQueriesPersonTable
parameter_list|()
block|{
return|return
literal|"deletedPkQuery=''SELECT ID FROM PEOPLE WHERE DELETED='Y' AND last_modified&gt;='${dih.last_index_time}' '' "
operator|+
literal|"deltaImportQuery=''SELECT ID, NAME, COUNTRY_CODE FROM PEOPLE where ID=${dih.delta.ID} '' "
operator|+
literal|"deltaQuery=''"
operator|+
literal|"SELECT ID FROM PEOPLE WHERE DELETED!='Y' AND last_modified&gt;='${dih.last_index_time}' "
operator|+
operator|(
name|useParentDeltaQueryParam
condition|?
literal|""
else|:
literal|"UNION DISTINCT "
operator|+
literal|"SELECT ID FROM PEOPLE WHERE DELETED!='Y' AND COUNTRY_CODE IN (SELECT CODE FROM COUNTRIES WHERE last_modified&gt;='${dih.last_index_time}') "
operator|)
operator|+
literal|"'' "
return|;
block|}
annotation|@
name|Override
DECL|method|deltaQueriesCountryTable
specifier|protected
name|String
name|deltaQueriesCountryTable
parameter_list|()
block|{
if|if
condition|(
name|useParentDeltaQueryParam
condition|)
block|{
return|return
literal|"deltaQuery=''SELECT CODE FROM COUNTRIES WHERE DELETED != 'Y' AND last_modified&gt;='${dih.last_index_time}' ''  "
operator|+
literal|"parentDeltaQuery=''SELECT ID FROM PEOPLE WHERE DELETED != 'Y' AND COUNTRY_CODE='${Countries.CODE}' '' "
return|;
block|}
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

