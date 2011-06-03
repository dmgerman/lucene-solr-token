begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package

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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|CharArr
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
name|search
operator|.
name|MutableValue
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
name|search
operator|.
name|MutableValueFloat
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
name|search
operator|.
name|MutableValueStr
import|;
end_import

begin_class
DECL|class|StrDocValues
specifier|public
specifier|abstract
class|class
name|StrDocValues
extends|extends
name|DocValues
block|{
DECL|field|vs
specifier|protected
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|method|StrDocValues
specifier|public
name|StrDocValues
parameter_list|(
name|ValueSource
name|vs
parameter_list|)
block|{
name|this
operator|.
name|vs
operator|=
name|vs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
specifier|abstract
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|objectVal
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|exists
argument_list|(
name|doc
argument_list|)
condition|?
name|strVal
argument_list|(
name|doc
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|boolVal
specifier|public
name|boolean
name|boolVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|exists
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vs
operator|.
name|description
argument_list|()
operator|+
literal|"='"
operator|+
name|strVal
argument_list|(
name|doc
argument_list|)
operator|+
literal|"'"
return|;
block|}
annotation|@
name|Override
DECL|method|getValueFiller
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueStr
name|mval
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|exists
operator|=
name|bytesVal
argument_list|(
name|doc
argument_list|,
name|mval
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

