/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.yarn.boot.cli;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import joptsimple.OptionSet;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.yarn.boot.app.ClientApplicationRunner;
import org.springframework.yarn.boot.cli.YarnSubmitCommand.SubmitOptionHandler;

public class YarnSubmitCommandTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testDefaultOptionHelp() {
		YarnPushCommand command = new YarnPushCommand();
		assertThat(command.getHelp(), containsString("-v, --application-version"));
	}

	@Test
	public void testShouldNotFail() throws Exception {
		NoRunSubmitOptionHandler handler = new NoRunSubmitOptionHandler();
		YarnSubmitCommand command = new YarnSubmitCommand(handler);
		command.run();
		assertThat(handler.appVersion, is("app"));
	}

	@Test
	public void testCustomOptionHandlerFailure() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(is("no jee"));
		CustomSubmitOptionHandler handler = new CustomSubmitOptionHandler();
		YarnSubmitCommand command = new YarnSubmitCommand(handler);
		command.run("-v", "foo");
	}

	@Test
	public void testNoArgsRun() throws Exception {
		NoHandleApplicationOptionHandler handler = new NoHandleApplicationOptionHandler();
		YarnSubmitCommand command = new YarnSubmitCommand(handler);
		command.run();
		assertThat(handler.output, is("output"));
	}

	private static class CustomSubmitOptionHandler extends SubmitOptionHandler {

		@Override
		protected void verifyOptionSet(OptionSet options) throws Exception {
			String appVersion = options.valueOf(getApplicationVersionOption());
			if (!appVersion.startsWith("jee")) {
				throw new IllegalArgumentException("no jee");
			}
		}

	}

	private static class NoRunSubmitOptionHandler extends SubmitOptionHandler {

		String appVersion;

		@Override
		protected void runApplication(OptionSet options) throws Exception {
			appVersion = options.valueOf(getApplicationVersionOption());
		}

	}

	private static class NoHandleApplicationOptionHandler extends SubmitOptionHandler {

		String output;

		@Override
		protected void handleApplicationRun(ClientApplicationRunner<ApplicationId> app) {
			this.output = "output";
		}

	}

}
