package sg.edu.nus.comp.cs3213.filters;

import sg.edu.nus.comp.cs3213.Filter;
import sg.edu.nus.comp.cs3213.WorkUnit;

public class UpperCaseFilter extends Filter {

	@Override
	protected WorkUnit process(WorkUnit work) {
		work.setData(work.getData().toUpperCase());
		return work;
	}

}
