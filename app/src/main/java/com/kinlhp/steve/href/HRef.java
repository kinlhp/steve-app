package com.kinlhp.steve.href;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by kin on 8/14/17.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class HRef implements Serializable {
	private static final long serialVersionUID = 1516555064872455773L;

	@SerializedName(value = "href")
	private String href;
}
