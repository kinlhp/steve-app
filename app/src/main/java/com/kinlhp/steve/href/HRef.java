package com.kinlhp.steve.href;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class HRef implements Serializable {
	private static final long serialVersionUID = -9219691613497393354L;

	@SerializedName(value = "href")
	private String href;
}
