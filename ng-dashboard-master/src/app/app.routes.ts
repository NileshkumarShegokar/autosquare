import {HomeComponent} from "./pages/home/home.component";
import {FeatureComponent} from "./pages/feature/feature.component";

export const appRoutes=[
    {
        path:'',
        redirectTo:'feature',
        pathMatch:'full'
    },
    {
        path: 'home',
        component: HomeComponent
    },{
        path: 'feature',
        component: FeatureComponent
    },
    {
        path: 'others',
        loadChildren:'./pages/others/others.module#OthersModule',
    },
];