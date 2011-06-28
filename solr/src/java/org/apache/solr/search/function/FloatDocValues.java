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
name|common
operator|.
name|mutable
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
name|lucene
operator|.
name|common
operator|.
name|mutable
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
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|DocValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import

begin_class
DECL|class|FloatDocValues
specifier|public
specifier|abstract
class|class
name|FloatDocValues
extends|extends
name|DocValues
block|{
DECL|field|vs
specifier|protected
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|method|FloatDocValues
specifier|public
name|FloatDocValues
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
DECL|method|byteVal
specifier|public
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shortVal
specifier|public
name|short
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|floatVal
specifier|public
specifier|abstract
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
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
name|floatVal
argument_list|(
name|doc
argument_list|)
else|:
literal|null
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
literal|'='
operator|+
name|strVal
argument_list|(
name|doc
argument_list|)
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
name|MutableValueFloat
name|mval
init|=
operator|new
name|MutableValueFloat
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
name|value
operator|=
name|floatVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
name|exists
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

