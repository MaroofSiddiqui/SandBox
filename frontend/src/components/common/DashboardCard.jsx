// Reusable dashboard card component.
// We will use this for every statistic shown on the dashboard.

import "../../styles/card.css";

function DashboardCard({

    title,
    value,
    icon,
    color

}){

    return(

        <div
            className="dashboard-card"
            style={{
                borderLeft:`6px solid ${color}`
            }}
        >

            <div>

                <p className="card-title">

                    {title}

                </p>

                <h2>

                    {value}

                </h2>

            </div>

            <div
                className="card-icon"
                style={{
                    color:color
                }}
            >

                {icon}

            </div>

        </div>

    );

}

export default DashboardCard;