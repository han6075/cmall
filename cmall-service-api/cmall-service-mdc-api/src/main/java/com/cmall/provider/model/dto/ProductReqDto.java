
package com.cmall.provider.model.dto;


import com.cmall.base.dto.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class Product req dto.
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class ProductReqDto extends BaseQuery {
	private static final long serialVersionUID = -9070173642901418263L;
	@ApiModelProperty(value = "分类ID")
	private Long categoryId;
	@ApiModelProperty(value = "关键词")
	private String keyword;
}
